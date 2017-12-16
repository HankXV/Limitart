/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.net.binary;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.Func1;
import org.slingerxv.limitart.logging.Loggers;
import org.slingerxv.limitart.net.Session;
import org.slingerxv.limitart.reflectasm.ConstructorAccess;
import org.slingerxv.limitart.reflectasm.MethodAccess;
import org.slingerxv.limitart.util.ReflectionUtil;

/**
 * 消息工厂
 *
 * @author hank
 */
public class BinaryMessageFactory {
    private static Logger log = Loggers.create(BinaryMessageFactory.class);
    // !!这里的asm应用经测试在JAVA8下最优
    private final Map<Short, MessageContext> msgs = new HashMap<>();
    private final Map<Class<?>, Object> managerInstances = new HashMap<>();
    private final Map<Class<?>, MethodAccess> methods = new HashMap<>();

    private BinaryMessageFactory() {
    }

    /**
     * 创造一个空的消息工厂
     *
     * @return
     */
    public static BinaryMessageFactory createEmpty() {
        return new BinaryMessageFactory();
    }

    /**
     * 通过包扫描创建消息工厂
     *
     * @param scanPackage 包名
     * @return
     * @throws ReflectiveOperationException
     * @throws IOException
     * @throws BinaryMessageIDDuplicatedException
     */
    public static BinaryMessageFactory create(String scanPackage)
            throws IOException, ReflectiveOperationException, BinaryMessageIDDuplicatedException {
        Conditions.notNull(scanPackage, "scanPackage");
        BinaryMessageFactory factory = new BinaryMessageFactory();
        List<Class<?>> classesByPackage = ReflectionUtil.getClassesByPackage(scanPackage, Object.class);
        for (Class<?> clazz : classesByPackage) {
            factory.registerManager(clazz, null);
        }
        return factory;
    }

    /**
     * 通过扫描包创建消息工厂
     *
     * @param scanPackage
     * @param confirmInstance 指定manager的 实例
     * @return
     * @throws IOException
     * @throws ReflectiveOperationException
     * @throws BinaryMessageIDDuplicatedException
     */
    public static BinaryMessageFactory create(String scanPackage, Func1<Class<?>, Object> confirmInstance)
            throws IOException, ReflectiveOperationException, BinaryMessageIDDuplicatedException {
        Conditions.notNull(scanPackage, "scanPackage");
        BinaryMessageFactory factory = new BinaryMessageFactory();
        List<Class<?>> classesByPackage = ReflectionUtil.getClassesByPackage(scanPackage, Object.class);
        for (Class<?> clazz : classesByPackage) {
            factory.registerManager(clazz, confirmInstance);
        }
        return factory;
    }

    /**
     * 注册一个manager
     *
     * @param clazz
     * @throws BinaryMessageIDDuplicatedException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public BinaryMessageFactory registerManager(Class<?> clazz)
            throws IllegalAccessException, InstantiationException, BinaryMessageIDDuplicatedException {
        return registerManager(clazz, null);
    }

    /**
     * 注册一个manager
     *
     * @param clazz           类
     * @param confirmInstance 指定manager的 实例
     * @throws IllegalAccessException
     * @throws BinaryMessageIDDuplicatedException
     * @throws InstantiationException
     */
    public BinaryMessageFactory registerManager(Class<?> clazz, Func1<Class<?>, Object> confirmInstance)
            throws IllegalAccessException, BinaryMessageIDDuplicatedException, InstantiationException {
        BinaryManager manager = clazz.getAnnotation(BinaryManager.class);
        if (manager == null) {
            return this;
        }
        // 扫描方法
        MethodAccess methodAccess = MethodAccess.get(clazz);
        for (int i = 0; i < methodAccess.getMethods().size(); ++i) {
            Method method = methodAccess.getMethods().get(i);
            BinaryHandler handler = method.getAnnotation(BinaryHandler.class);
            if (handler == null) {
                continue;
            }
            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalAccessError("method must be public:" + clazz.getName() + "."
                        + ReflectionUtil.getMethodOverloadName(method));
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IllegalArgumentException(clazz.getName() + "." + ReflectionUtil.getMethodOverloadName(method)
                        + " params length must be only one");
            }
            Class<?> paramType = parameterTypes[0];
            if (paramType != BinaryRequestParam.class) {
                throw new IllegalAccessException(clazz.getName() + "." + ReflectionUtil.getMethodOverloadName(method)
                        + " param can only be " + BinaryRequestParam.class.getName());
            }
            Class<? extends BinaryMessage> messageType = handler.value();
            ConstructorAccess<? extends BinaryMessage> constructorAccess = ConstructorAccess.get(messageType);
            BinaryMessage messageInstance = constructorAccess.newInstance();
            short messageID = messageInstance.messageID();
            if (msgs.containsKey(messageID)) {
                throw new BinaryMessageIDDuplicatedException(messageID);
            }
            MessageContext messageContext = new MessageContext();
            messageContext.conAccess = constructorAccess;
            messageContext.managerClazz = clazz;
            messageContext.methodIndex = i;
            msgs.put(messageID, messageContext);
            if (!managerInstances.containsKey(clazz)) {
                if (confirmInstance != null) {
                    managerInstances.put(clazz, confirmInstance.run(clazz));
                } else {
                    managerInstances.put(clazz, clazz.newInstance());
                }
            }
            if (!methods.containsKey(clazz)) {
                methods.put(clazz, methodAccess);
            }
            log.info("register msg " + messageType.getName() + " at " + clazz.getName());
        }
        return this;
    }

    /**
     * 执行消息的处理
     *
     * @param session
     * @param msg
     */
    public void invokeMethod(Session session, BinaryMessage msg) {
        Conditions.notNull(msg, "msg");
        short messageID = msg.messageID();
        MessageContext messageContext = msgs.get(messageID);
        if (messageContext == null) {
            log.error(session.remoteAddress() + " message empty,id:" + BinaryMessages.ID2String(messageID));
            // 消息上下文不存在
            return;
        }
        BinaryRequestParam param = new BinaryRequestParam(session, msg);
        MethodAccess methodAccess = methods.get(messageContext.managerClazz);
        Object object = managerInstances.get(messageContext.managerClazz);
        methodAccess.invoke(object, messageContext.methodIndex, param);
    }

    /**
     * 替换掉manager的实例
     *
     * @param msg
     * @param newInstance
     */
    public void replaceInstance(Class<?> managerClass, BinaryMessage msg, Object newInstance) {
        Conditions.notNull(managerClass, "managerClass");
        Conditions.notNull(msg, "msg");
        Conditions.notNull(newInstance, "newInstance");
        if (managerInstances.containsKey(managerClass)) {
            managerInstances.put(managerClass, newInstance);
        }
    }

    /**
     * 根据ID获取一个消息实例
     *
     * @param msgId
     * @return
     * @throws ReflectiveOperationException
     */
    public BinaryMessage msgInstance(short msgId) {
        if (!msgs.containsKey(msgId)) {
            return null;
        }
        MessageContext messageContext = msgs.get(msgId);
        return messageContext.conAccess.newInstance();
    }

    private static class MessageContext {
        private ConstructorAccess<? extends BinaryMessage> conAccess;
        private Class<?> managerClazz;
        private int methodIndex;
    }
}

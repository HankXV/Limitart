/*
 *
 *  * Copyright (c) 2016-present The Limitart Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package top.limitart.rpc;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * RPC服务提供者
 *
 * @author hank
 */
public class AbstractRPCProvider {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractRPCProvider.class);
    private Map<String, RpcServiceInstance> services = new HashMap<>();

    private void load() throws  ReflectiveOperationException {
        List<Class<?>> classesByPackage = new ArrayList<>();
        for (String temp : this.config.getServicePackages()) {
            log.info("开始在包：" + temp + "下查找接口...");
            classesByPackage.addAll(ReflectionUtil.getClassesByPackage(temp, Object.class));
        }
        // RPC接口集合
        Map<Class<?>, Map<String, Method>> rpcInterfaces = new HashMap<>();
        // 查找所有RPC接口
        for (Class<?> clazz : classesByPackage) {
            // 必须是一个接口
            ServiceX annotation = clazz.getAnnotation(ServiceX.class);
            if (annotation == null) {
                continue;
            }
            if (!clazz.isInterface()) {
                throw new ServiceXProxyException(clazz.getName() + "RPC服务器必须是一个接口！");
            }
            // 检查参数是否符合标准
            String provider = annotation.provider();
            if (StringUtil.isEmptyOrNull(provider)) {
                throw new ServiceXProxyException("RPC接口提供商不能为空！");
            }
            String serviceName = RpcUtil.getServiceName(new RpcProviderName(provider), clazz);
            if (services.containsKey(serviceName)) {
                throw new ServiceXProxyException("服务名：" + serviceName + "重复！");
            }
            // 检查方法
            Method[] methods = clazz.getMethods();
            Map<String, Method> methodSet = new HashMap<>();
            // 检查方法参数是否合法
            for (Method method : methods) {
                String methodOverloadName = ReflectionUtil.getMethodOverloadName(method);
                // 检查参数
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class<?> paramsType : parameterTypes) {
                    RpcUtil.checkParamType(paramsType);

                }
                // 检查返回参数是否合法
                RpcUtil.checkParamType(method.getReturnType());
                // 异常抛出检查
                Class<?>[] exceptionTypes = method.getExceptionTypes();
                if (exceptionTypes == null || exceptionTypes.length < 1) {
                    throw new ServiceXProxyException("类" + clazz.getName() + "的方法" + methodOverloadName + "必须要抛出异常："
                            + Exception.class.getName());
                }
                boolean exOk = false;
                for (Class<?> ex : exceptionTypes) {
                    if (ex == Exception.class) {
                        exOk = true;
                    }
                }
                if (!exOk) {
                    throw new ServiceXProxyException("类" + clazz.getName() + "的方法" + methodOverloadName + "的异常抛出必须有："
                            + Exception.class.getName());
                }
                methodSet.put(ReflectionUtil.getMethodOverloadName(method), method);
            }
            rpcInterfaces.put(clazz, methodSet);
        }
        // 查找RPC接口的实现类
        List<Class<?>> classesByPackage2 = ReflectionUtil.getClassesByPackage(this.config.getServiceImplPackages(),
                Object.class);
        log.info("开始在包：" + this.config.getServiceImplPackages() + "下查找接口实现...");
        for (Class<?> clazz : classesByPackage2) {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces == null || interfaces.length < 1) {
                continue;
            }
            // 检查实现的接口实例的所有RPC服务
            Map<String, Class<?>> serviceNames = new HashMap<>();
            Object instance = null;
            // 遍历接口（主要处理一个实例，实现了多个RPC接口的情况）
            for (Class<?> temp : interfaces) {
                Map<String, Method> hashMap = rpcInterfaces.get(temp);
                // 没有RPC服务
                if (hashMap == null) {
                    continue;
                }
                ServiceX annotation = temp.getAnnotation(ServiceX.class);
                // 此类有实现此RPC接口
                serviceNames.put(RpcUtil.getServiceName(new RpcProviderName(annotation.provider()), temp), temp);
                if (instance == null) {
                    instance = clazz.newInstance();
                }
            }
            // 如果查找到了实例
            if (instance != null && !serviceNames.isEmpty()) {
                for (Entry<String, Class<?>> entry : serviceNames.entrySet()) {
                    String serviceName = entry.getKey();
                    if (services.containsKey(serviceName)) {
                        throw new ServiceXProxyException("服务：" + serviceName + "发现了多个实现类：" + instance);
                    }
                    RpcServiceInstance data = new RpcServiceInstance();
                    data.setInstance(instance);
                    Class<?> value = entry.getValue();
                    data.getMethods().putAll(rpcInterfaces.get(value));
                    services.put(serviceName, data);
                    log.info("发现服务：" + serviceName + "，实例名称："
                            + (clazz.getName() + "@" + Integer.toHexString(instance.hashCode())));
                }
            }
        }
    }

    /**
     * 执行RPC消费者请求的方法
     *
     * @param context
     * @param requestId
     * @param moduleName
     * @param methodName
     * @param params
     */
    private void executeRPC(Channel channel, int requestId, String moduleName, String methodName, List<Object> params)
            throws Exception {
        RpcResultServerMessage msg = new RpcResultServerMessage();
        msg.setRequestId(requestId);
        msg.setErrorCode(0);
        try {
            RpcServiceInstance serviceInstanceData = services.get(moduleName);
            if (serviceInstanceData == null) {
                log.error("RPC消费者：" + channel.remoteAddress() + "发送了未知的服务名：" + moduleName);
                msg.setErrorCode(ServiceError.SERVER_HAS_NO_MODULE);
                return;
            }
            Method method = serviceInstanceData.getMethods().get(methodName);
            if (method == null) {
                log.error("RPC消费者：" + channel.remoteAddress() + "发送了未知的方法名：" + methodName + "，服务名为：" + moduleName);
                msg.setErrorCode(ServiceError.SERVER_HAS_NO_METHOD);
                return;
            }
            if (msg.getErrorCode() == 0) {
                try {
                    Object result = method.invoke(serviceInstanceData.self(), params.toArray());
                    if (result != null) {
                        msg.setReturnType(result.getClass().getName());
                        msg.setReturnVal(result);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } finally {
            try {
                server.sendMessage(channel, msg, null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 获取服务实体
     *
     * @param provider
     * @param clazz
     * @return
     * @throws ServiceXProxyException
     */
    @SuppressWarnings("unchecked")
    public <T> T getServiceInstance(RpcProviderName provider, Class<T> clazz) throws ServiceXProxyException {
        ServiceX annotation = clazz.getAnnotation(ServiceX.class);
        if (annotation == null) {
            throw new ServiceXProxyException(clazz.getName() + "is not ServiceX!");
        }
        RpcServiceInstance serviceInstanceData = services.get(RpcUtil.getServiceName(provider, clazz));
        if (serviceInstanceData == null) {
            return null;
        }
        return (T) serviceInstanceData.self();
    }
}

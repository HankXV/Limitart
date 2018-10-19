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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Conditions;
import top.limitart.base.Proc1;
import top.limitart.util.ReflectionUtil;
import top.limitart.util.StringUtil;

import java.io.IOException;
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
public abstract class AbstractRPCProvider {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractRPCProvider.class);
    private Map<RPCModuleName, RpcServiceInstance> services = new HashMap<>();

    public AbstractRPCProvider(String... packages) throws ReflectiveOperationException, IOException, PRCServiceProxyException {
        String[] params = packages;
        if (params.length == 0) {
            params = new String[]{""};
        }
        List<Class<?>> classesByPackage = new ArrayList<>();
        for (String temp : params) {
            classesByPackage.addAll(ReflectionUtil.getClassesBySuperClass(temp, Object.class));
        }
        // RPC接口集合
        Map<Class<?>, Map<String, Method>> rpcInterfaces = new HashMap<>();
        // 查找所有RPC接口
        for (Class<?> clzz : classesByPackage) {
            RPCService annotation = clzz.getAnnotation(RPCService.class);
            if (annotation == null) {
                continue;
            }
            if (!clzz.isInterface()) {
                throw new PRCServiceProxyException("RPC service must be an interface,error clazz:" + clzz.getName());
            }
            String provider = annotation.value();
            if (StringUtil.empty(provider)) {
                throw new PRCServiceProxyException("service：" + clzz.getName() + " provider null！");
            }
            // 检查方法
            Method[] methods = clzz.getMethods();
            Map<String, Method> methodSet = new HashMap<>();
            // 检查方法参数是否合法
            for (Method method : methods) {
                String methodOverloadName = ReflectionUtil.getMethodOverloadName(method);
                // 检查参数
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Conditions.args(canTransferedType(parameterTypes[i]), "{} param type error on index:{}", methodOverloadName, i);
                }
                Conditions.args(canTransferedType(method.getReturnType()), "{} param type error on return", methodOverloadName);
                // 异常抛出检查
                Class<?>[] exceptionTypes = method.getExceptionTypes();
                if (exceptionTypes == null || exceptionTypes.length < 1) {
                    throw new PRCServiceProxyException("class " + clzz.getName() + " method " + methodOverloadName + " must contains Exception");
                }
                boolean exOk = false;
                for (Class<?> ex : exceptionTypes) {
                    if (ex == Exception.class) {
                        exOk = true;
                    }
                }
                if (!exOk) {
                    throw new PRCServiceProxyException("class " + clzz.getName() + " method " + methodOverloadName + " must contains Exception"
                    );
                }
                methodSet.put(methodOverloadName, method);
            }
            rpcInterfaces.put(clzz, methodSet);
        }
        // 查找RPC接口的实现类
        for (Class<?> clazz : classesByPackage) {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces == null || interfaces.length < 1) {
                continue;
            }
            // 检查实现的接口实例的所有RPC服务
            Map<RPCModuleName, Class<?>> serviceNames = new HashMap<>();
            Object instance = null;
            // 遍历接口（主要处理一个实例，实现了多个RPC接口的情况）
            for (Class<?> temp : interfaces) {
                Map<String, Method> hashMap = rpcInterfaces.get(temp);
                // 没有RPC服务
                if (hashMap == null) {
                    continue;
                }
                RPCService annotation = temp.getAnnotation(RPCService.class);
                // 此类有实现此RPC接口
                serviceNames.put(StringUtil.empty(annotation.module()) ? new RPCModuleName(annotation.value(), temp) : new RPCModuleName(annotation.value(), annotation.module()), temp);
                if (instance == null) {
                    instance = clazz.newInstance();
                }
            }
            // 如果查找到了实例
            if (instance != null && !serviceNames.isEmpty()) {
                for (Entry<RPCModuleName, Class<?>> entry : serviceNames.entrySet()) {
                    RPCModuleName serviceName = entry.getKey();
                    if (services.containsKey(serviceName)) {
                        throw new PRCServiceProxyException("service:" + serviceName + " duplicated:" + instance);
                    }
                    RpcServiceInstance data = new RpcServiceInstance();
                    data.instance = instance;
                    Class<?> value = entry.getValue();
                    data.methods.putAll(rpcInterfaces.get(value));
                    services.put(serviceName, data);
                    LOGGER.info("find RPC service impl:" + serviceName + ",impl name:"
                            + (clazz.getName() + "@" + Integer.toHexString(instance.hashCode())));
                }
            }
        }
    }

    protected abstract boolean canTransferedType(Class<?> returnType);

    /**
     * 执行RPC消费者请求的方法
     *
     * @param requestId
     * @param params
     */
    public void executeRPC(int requestId, RPCServiceName serviceName, List<Object> params, Proc1<RPCResponse> resultCallback)
            throws Exception {
        int errorCode = 0;
        Object returnVal = null;
        try {
            RPCModuleName rpcModuleName = serviceName.getModuleName();
            RpcServiceInstance serviceInstanceData = services.get(rpcModuleName);
            if (serviceInstanceData == null) {
                LOGGER.error("module name is not exist:{}", rpcModuleName);
                errorCode = RPCServiceErrorCode.SERVER_HAS_NO_MODULE;
                return;
            }
            Method method = serviceInstanceData.methods.get(serviceName.getMethodName());
            if (method == null) {
                LOGGER.error("can not find method {} from {}", serviceName.getMethodName(), rpcModuleName);
                errorCode = RPCServiceErrorCode.SERVER_HAS_NO_METHOD;
                return;
            }
            if (errorCode == RPCServiceErrorCode.SUCCESS) {
                try {
                    Object result = method.invoke(serviceInstanceData.instance, params.toArray());
                    if (result != null) {
                        returnVal = result;
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } finally {
            resultCallback.run(new RPCResponse(requestId, errorCode, returnVal));
        }
    }


    private class RpcServiceInstance {
        private Object instance;
        private Map<String, Method> methods = new HashMap<>();
    }
}
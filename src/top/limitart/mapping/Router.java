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
package top.limitart.mapping;


import top.limitart.base.Func;
import top.limitart.base.Func1;
import top.limitart.base.Proc1;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 路由器
 *
 * @author hank
 * @version 2018/10/8 0008 20:08
 */
public interface Router<ID, R extends Request<ID>, C extends RequestContext<R>> {

    /**
     * 创造一个空的消息工厂
     *
     * @return
     */
    static Router empty() {
        return RouterImpl.empty();
    }

    /**
     * 通过包扫描创建消息工厂
     *
     * @param scanPackage 包名
     * @return
     * @throws ReflectiveOperationException
     * @throws IOException
     */
    static Router create(String scanPackage) throws RequestIDDuplicatedException, ReflectiveOperationException, IOException {
        return RouterImpl.create(scanPackage);
    }

    /**
     * 通过扫描包创建消息工厂
     *
     * @param scanPackage
     * @param confirmInstance 指定manager的 实例
     * @return
     * @throws IOException
     * @throws ReflectiveOperationException
     */
    static Router create(String scanPackage, Func1<Class<?>, Object> confirmInstance) throws Exception {
        return RouterImpl.create(scanPackage, confirmInstance);
    }

    /**
     * 注册一个manager
     *
     * @param mapperClass
     */
    Router registerMapperClass(Class<?> mapperClass) throws Exception;

    /**
     * 注册一个manager
     *
     * @param mapperClass     类
     * @param confirmInstance 指定manager的实例
     */
    Router registerMapperClass(Class<?> mapperClass, Func1<Class<?>, Object> confirmInstance) throws Exception;

    /**
     * 替换掉manager的实例
     *
     * @param request
     * @param newInstance
     */
    void replaceInstance(Class<?> mapperClass, R request, Object newInstance) throws Exception;

    /**
     * 根据ID获取一个消息实例
     *
     * @param requestClass
     * @return
     * @throws ReflectiveOperationException
     */
    R requestInstance(Class<R> requestClass) throws Exception;

    R requestInstance(ID id) throws Exception;

    void request(R r, Func<C> contextInstance, Proc1<MethodInvoker> proc);

    interface MethodInvoker {
        void invoke();
    }
}

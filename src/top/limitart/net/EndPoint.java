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
package top.limitart.net;

/**
 * 端点
 *
 * @param <IN>  需要端点处理的输入信息
 * @param <OUT> 端点处理后的输出信息
 * @author hank
 * @version 2018/10/9 0009 19:38
 */
public interface EndPoint<IN, OUT> {
    /**
     * 端点名称
     *
     * @return
     */
    String name();

    /**
     * 启动端点
     */
    EndPoint start(AddressPair addressPair) throws Exception;

    /**
     * 停止端点
     */
    EndPoint stop() throws Exception;

    /**
     * 将端点内部可读信息转化为外部可传输信息
     *
     * @param in
     * @return
     * @throws Exception
     */
    OUT toOutputFinal(IN in) throws Exception;

    /**
     * 将端点外部可传输信息转化为内部可读信息
     *
     * @param out
     * @return
     * @throws Exception
     */
    IN toInputFinal(OUT out) throws Exception;
}

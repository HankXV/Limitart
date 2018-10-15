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
 * 端点类型
 *
 * @author hank
 * @version 2018/10/15 0015 11:58
 */
public enum NettyEndPointType {
    /**
     * 网络服务器
     */
    SERVER_REMOTE {
        @Override
        boolean server() {
            return true;
        }

        @Override
        boolean local() {
            return false;
        }
    },
    /**
     * 网络客户端
     */
    CLIENT_REMOTE {
        @Override
        boolean server() {
            return false;
        }

        @Override
        boolean local() {
            return false;
        }
    },
    /**
     * 进程内服务器
     */
    SERVER_LOCAL {
        @Override
        boolean server() {
            return true;
        }

        @Override
        boolean local() {
            return true;
        }
    },
    /**
     * 进程内客户端
     */
    CLIENT_LOCAL {
        @Override
        boolean server() {
            return false;
        }

        @Override
        boolean local() {
            return true;
        }
    },;

    abstract boolean server();

    abstract boolean local();

    public static NettyEndPointType defaultServer() {
        return NettyEndPointType.SERVER_REMOTE;
    }

    public static NettyEndPointType defaultClient() {
        return NettyEndPointType.CLIENT_REMOTE;
    }
}

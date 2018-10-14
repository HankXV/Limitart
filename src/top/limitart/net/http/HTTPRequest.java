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

package top.limitart.net.http;

import io.netty.handler.codec.http.HttpMethod;
import top.limitart.collections.ConstraintMap;
import top.limitart.collections.ImmutableMap;

import java.util.Map;

/**
 * Created by Hank on 2018/10/14
 */
public class HTTPRequest {
    private HttpMethod method;
    private String url;
    private Map<String, String> params;

    public HTTPRequest(HttpMethod method, String url, Map<String, String> params) {
        this.method = method;
        this.url = url;
        this.params = ImmutableMap.of(params);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String
            > getParams() {
        return params;
    }
}

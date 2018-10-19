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
package top.limitart.rpc;

/**
 * RPC执行结果
 *
 * @author hank
 * @version 2018/10/18 0018 20:12
 */
public class RPCResponse {
    private int requestID;
    private int errorCode;
    private String returnType;
    private Object returnVal;

    public RPCResponse(int requestID, int errorCode, Object returnVal) {
        this.requestID = requestID;
        this.errorCode = errorCode;
        this.returnVal = returnVal;
        if (returnVal != null) {
            this.returnType = returnVal.getClass().getName();
        }
    }

    public int getRequestID() {
        return requestID;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getReturnType() {
        return returnType;
    }

    public Object getReturnVal() {
        return returnVal;
    }
}

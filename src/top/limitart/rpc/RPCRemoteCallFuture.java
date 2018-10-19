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

import top.limitart.base.Proc1;

import java.util.concurrent.CountDownLatch;

/**
 * RPC远程调用异步结果
 *
 * @author hank
 * @version 2018/10/18 0018 17:15
 */
public class RPCRemoteCallFuture extends CountDownLatch {
    private int requestID;
    private Proc1<Object> callback;


    private volatile boolean completed = false;
    private Object returnVal;
    private int errorCode;

    public RPCRemoteCallFuture(int requestID, Proc1<Object> callback) {
        super(1);
        this.requestID = requestID;
        this.callback = callback;
    }

    public int getRequestID() {
        return requestID;
    }

    public Proc1<Object> getCallback() {
        return callback;
    }

    public boolean completed() {
        return this.completed;
    }

    public void complete() {
        this.completed = true;
    }

    public Object getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Object returnVal) {
        this.returnVal = returnVal;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}

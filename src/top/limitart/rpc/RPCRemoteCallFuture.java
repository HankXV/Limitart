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

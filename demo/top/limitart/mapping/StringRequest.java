package top.limitart.mapping;


/**
 * @author hank
 * @version 2018/10/8 0008 21:21
 */
public class StringRequest implements Request<Short> {
    private String msg;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public Short id() {
        return 1;
    }
}

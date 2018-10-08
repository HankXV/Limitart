package top.limitart.mapping;


/**
 * @author hank
 * @version 2018/10/8 0008 21:25
 */
public class RouterDemo {
    public static void main(String[] args) throws Exception {
        Router<Short, StringRequest, StringQuestParam> router = Router.empty();
        router.registerMapperClass(StringMapperClass.class);
        StringRequest newRequest = new StringRequest();
        newRequest.setMsg("hank!!!");
        router.request(newRequest, () -> new StringQuestParam(newRequest), i -> i.invoke());
    }
}

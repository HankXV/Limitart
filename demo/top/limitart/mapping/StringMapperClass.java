package top.limitart.mapping;

/**
 * @author hank
 * @version 2018/10/8 0008 21:23
 */
@MapperClass
public class StringMapperClass {
    @Mapper(StringRequest.class)
    public void onMsg(StringQuestParam param) {
        StringRequest request = param.msg();
        System.out.println(request.getMsg());
    }
}

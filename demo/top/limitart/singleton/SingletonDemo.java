package top.limitart.singleton;

public class SingletonDemo {
    @Ref
    SingletonB singletonB;
    @Ref
    SingletonA singletonA;
    public void say(){
        singletonB.say();
        singletonA.say();
    }
    public static void main(String[] args) {
        Singletons.create().search(Thread.currentThread().getContextClassLoader()).instance(SingletonDemo.class).say();
    }
}

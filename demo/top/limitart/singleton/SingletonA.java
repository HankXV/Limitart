package top.limitart.singleton;

@Singleton
public class SingletonA {
    @Ref
    SingletonB singletonB;

    public void say() {
        System.out.println(singletonB);
    }
}

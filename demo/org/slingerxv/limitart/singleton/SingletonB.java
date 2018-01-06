package org.slingerxv.limitart.singleton;

@Singleton
public class SingletonB {
    @Ref
    SingletonA singletonA;

    public void say() {
        System.out.println(singletonA);
    }
}

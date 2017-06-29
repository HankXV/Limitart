package org.slingerxv.limitart.injection;

/**
 * @author Hank
 * @version 2017/11/11 22:41
 */
public class InjectionTest {
    public static void main(String[] args) {
        Injector.create().reg(IBean.class).as(Bean1.class).as(Bean2.class);
    }
}

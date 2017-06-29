package org.slingerxv.limitart.injection;

/**
 * @author Hank
 * @version 2017/11/11 22:40
 */
public class Bean2 implements IBean {
    @Override
    public void print() {
        System.out.println(Bean1.class.getName());
    }
}

package org.slingerxv.limitart.base;

import java.lang.annotation.*;

/**
 * 标记方法为必要的调用
 *
 * @author hanxiao
 * @version 2017/10/18
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface Necessary {
}

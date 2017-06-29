package org.slingerxv.limitart.base;

import java.lang.annotation.*;

/**
 * 标记方法为可选择的
 *
 * @author hank
 * @version 2017/10/18
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Optional {
}

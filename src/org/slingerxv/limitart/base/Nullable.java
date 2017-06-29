package org.slingerxv.limitart.base;

import java.lang.annotation.*;

/**
 * 标记某参数或返回值可能为空
 *
 * @author hank
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Nullable {
}


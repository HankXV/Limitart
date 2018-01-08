package org.slingerxv.limitart.collections;

import java.lang.annotation.*;

/**
 * 表示为线程安全的类或者方法
 *
 * @author hank
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ThreadSafe {
}

package org.slingerxv.limitart.collections;

import java.lang.annotation.*;

/**
 * 表示类或方法不是线程安全的
 *
 * @author hank
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ThreadUnsafe {
}

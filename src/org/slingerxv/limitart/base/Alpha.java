package org.slingerxv.limitart.base;

import java.lang.annotation.*;

/**
 * 被标记的代表为测试接口
 *
 * @author hank
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE
})
@Documented
public @interface Alpha {
}

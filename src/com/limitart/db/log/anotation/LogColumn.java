package com.limitart.db.log.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.limitart.db.log.define.SqlColumnType;


/**
 * 日志列注解
 * 
 * @author hank
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LogColumn {

	/**
	 * sql类型
	 * 
	 * @return sql类型
	 */
    SqlColumnType type() default SqlColumnType.MYSQL_int;

	/**
	 * 大小
	 * 
	 * @return 大小
	 */
    int size() default 0;

	/**
	 * 列注释
	 * 
	 * @return 列注释
	 */
    String comment() default "N/A";
}

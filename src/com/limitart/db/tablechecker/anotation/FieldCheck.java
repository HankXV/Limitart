package com.limitart.db.tablechecker.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.limitart.db.log.define.SqlColumnType;


/**
 * 字段对应表信息检查
 * 
 * @author hank
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldCheck {
	/**
	 * 表字段名
	 * 
	 * @return
	 */
	public String value() default "";

	/**
	 * 在sql里的类型
	 * 
	 * @return
	 */
	public SqlColumnType type() default SqlColumnType.MYSQL_int;

	public int size() default 0;

	public boolean isNullable() default true;

	/**
	 * 是否是主键
	 * 
	 * @return
	 */
	public boolean primary() default false;
}
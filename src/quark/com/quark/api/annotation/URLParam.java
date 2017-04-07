/**
 * 
 */
package com.quark.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kingsley
 *
 * @datetime 2014年11月29日 下午3:49:06
 */
@Repeatable(URLParams.class)
@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ElementType.FIELD,ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented//说明该注解将被包含在javadoc中  
public @interface URLParam {

	/**
	 * 请求参数名
	 * @return
	 */
	public String name();
	/**
	 * 解释
	 * @return
	 */
	public String explain();
	/**
	 * 测试值
	 * @return
	 */
	public String defaultValue();
	/**
	 * 类型
	 */
	public String type() default Type.String;
	
}

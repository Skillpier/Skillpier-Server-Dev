/**
 * 
 */
package com.quark.api.annotation;

import java.io.Serializable;

/**
 * @author kingsley
 * 
 * @info 返回值类型
 *
 * @datetime 2014年12月3日 下午8:00:40
 */
public class Type implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String Int = "int";
	public static final String Int_NotRequired = "int_NotRequired";
	public static final String Long = "long";
	public static final String Long_NotRequired  = "long_NotRequired";
	public static final String String = "String";
	public static final String String_NotRequired  = "String_NotRequired";
	public static final String File = "File";
	public static final String File_NotRequired  = "File_NotRequired";
	public static final String Date = "Date";
	public static final String Date_NotRequired  = "Date_NotRequired";
	
}

/**
 * 
 */
package com.quark.utils;

/**
 * @author kingsley
 * 
 *         2014年5月28日
 */
public class FileType {

	public static String get(String filename) {
		if (filename.endsWith(".jpeg") || filename.endsWith(".jpg")) {
			return "图片";
		} else if (filename.endsWith(".rar") || filename.endsWith(".zip")) {
			return "压缩文件";
		}
		return "未知格式";
	}
}

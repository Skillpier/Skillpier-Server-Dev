/**
 * 
 */
package com.quark.admin.controller;

import java.io.File;

import com.jfinal.core.Controller;

/**
 * @author kingsley
 *
 */
public class JavaFiles extends Controller {

	public void down(){
		String file = getPara("file");
		renderFile("/api/AndroidBeans/"+file);
	}
}

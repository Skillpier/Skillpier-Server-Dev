package com.quark.admin.controller;

import java.io.File;

import com.jfinal.core.Controller;

public class SwiftFiles extends Controller {

	public void down(){
		String file = getPara("file");
		renderFile("/api/IOSBeans/"+file);
	}
}

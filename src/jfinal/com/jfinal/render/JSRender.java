package com.jfinal.render;

import java.io.File;

public class JSRender extends StaticRender {

	private static final String contentType = "text/javascript;charset=utf-8";

	public JSRender(File file) {
		super(file, contentType);
	}

	public JSRender(File file, String contentType) {
		super(file, contentType);
	}
}

package com.jfinal.render;

import java.io.File;

public class CSSRender extends StaticRender {

	private static final String contentType = "text/css;charset=utf-8";

	public CSSRender(File file) {
		super(file, contentType);
	}

	public CSSRender(File file, String contentType) {
		super(file, contentType);
	}
}

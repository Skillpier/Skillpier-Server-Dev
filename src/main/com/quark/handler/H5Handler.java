package com.quark.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

public class H5Handler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		//处理rp请求
		 int index = target.lastIndexOf(".html");
		 if (target.indexOf('.') != -1) {
			 target = target.substring(0, index);
		}
		nextHandler.handle(target, request, response, isHandled);
	}
}

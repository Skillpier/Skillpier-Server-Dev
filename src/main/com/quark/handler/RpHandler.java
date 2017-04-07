package com.quark.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

public class RpHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		//处理rp请求
		if (!target.startsWith("/mobile")&&!target.startsWith("/skillopedia") ) {
			if (!target.startsWith("/rp/resource") && target.contains(".html")) {
				String uri = request.getRequestURI();
				target = target.replace(".html", "~html");
			}
		}
		nextHandler.handle(target, request, response, isHandled);
	}
}

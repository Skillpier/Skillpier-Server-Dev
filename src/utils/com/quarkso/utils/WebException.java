package com.quarkso.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.jfinal.core.Controller;
import com.quark.common.config;

public class WebException {

	public static void print2html(Controller controller, Exception e) {
		if (config.devMode) {
			try {
				controller.getResponse().setContentType(
						"text/html;charset=utf-8");
				PrintWriter writer = controller.getResponse().getWriter();
				// Guard against malicious overrides of Throwable.equals by
				// using a Set with identity equality semantics.
				Set<Throwable> dejaVu = Collections
						.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
				dejaVu.add(e.getCause());
				// Print our stack trace
				writer.print("<pre>");
				e.printStackTrace(writer);
				writer.print("</pre>");
				writer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}

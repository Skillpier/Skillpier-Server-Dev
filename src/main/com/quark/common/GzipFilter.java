package com.quark.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GzipFilter implements Filter {

	private ServletContext ctx;
	private Logger logger = Logger.getLogger(GzipFilter.class.getName());
	private String contextPath;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		String accept = req.getHeader("Accept-Encoding");
		InputStream in = null;
		contextPath = ctx.getContextPath();
		uri = uri.substring(contextPath.length());
		if (accept != null && accept.contains("gzip") && (in = ctx.getResourceAsStream(uri + ".gz")) != null) {
			logger.info("start getting gzip file "+uri);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] b = new byte[1024 * 8];
			int read = 0;
			while ((read = in.read(b)) >= 0) {
				bout.write(b, 0, read);
			}
			in.close();

			res.setHeader("Content-Encoding", "gzip");
			res.setContentType("application/javascript;charset=UTF-8");
			res.setContentLength(bout.size());

			ServletOutputStream out = res.getOutputStream();
			out.write(bout.toByteArray());
			out.flush();
			logger.info("finish getting gzip file "+uri);
			return;
		} else {   
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		ctx = config.getServletContext();
	}

}


/**
 * Copyright (c) 2014-2015, kksdapp.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jfinal.render;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FileRender.
 */
public class StaticRender {

	private static final long serialVersionUID = 4293616220202691369L;
	private File file;
	private String fileName;
	private static String fileDownloadPath;
	private static ServletContext servletContext;
	private String contentType;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public StaticRender(String fileStr, String contentType,
			HttpServletRequest req, HttpServletResponse resp) {
		this.contentType = contentType;
		System.out.println("contentType:"+contentType);
		request = req;
		response = resp;
		if (fileStr != null) {
			file = new File(fileStr);
		}
	}
	public StaticRender(File file,String contentType) {
		this.file = file;
		this.contentType = contentType;
	}

	public StaticRender(String fileName) {
		this.fileName = fileName;
	}

	static void init(String fileDownloadPath, ServletContext servletContext) {
		StaticRender.fileDownloadPath = fileDownloadPath;
		StaticRender.servletContext = servletContext;
	}

	public void render() {
		try {
			response.addHeader("Content-disposition", "attachment; filename="
					+ new String(file.getName().getBytes("GBK"), "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			response.addHeader("Content-disposition", "attachment; filename="
					+ file.getName());
		}
		response.setContentType(contentType);
		response.setContentLength((int) file.length());
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			outputStream = response.getOutputStream();
			byte[] buffer = new byte[1024];
			for (int n = -1; (n = inputStream.read(buffer)) != -1;) {
				outputStream.write(buffer, 0, n);
			}
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

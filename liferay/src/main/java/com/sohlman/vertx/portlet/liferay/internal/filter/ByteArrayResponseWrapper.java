package com.sohlman.vertx.portlet.liferay.internal.filter;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ByteArrayResponseWrapper extends HttpServletResponseWrapper {
	private ByteArrayPrintWriter output;

	public String toString() {
		return output.toString();
	}

	public ByteArrayResponseWrapper(HttpServletResponse response) {
		super(response);
		output = new ByteArrayPrintWriter();
	}

	public PrintWriter getWriter() {
		return output.getWriter();
	}

	public ServletOutputStream getOutputStream() {
		return output.getStream();
	}
}

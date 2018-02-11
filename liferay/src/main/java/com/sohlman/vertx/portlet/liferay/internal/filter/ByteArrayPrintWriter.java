package com.sohlman.vertx.portlet.liferay.internal.filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

public class ByteArrayPrintWriter {

	public PrintWriter getWriter() {
		return printWriter;
	}

	public ServletOutputStream getStream() {
		return servletOutputStream;
	}

	byte[] toByteArray() {
		return byteArrayOutputStream.toByteArray();
	}

	public String toString() {
		return new String(byteArrayOutputStream.toByteArray());
	}
	
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	private PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

	private ServletOutputStream servletOutputStream = new ByteArrayServletStream(byteArrayOutputStream);
}

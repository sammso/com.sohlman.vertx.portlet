package com.sohlman.vertx.portlet.liferay.internal.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class ByteArrayServletStream extends ServletOutputStream {
	
	ByteArrayServletStream(ByteArrayOutputStream byteArrayOutputStream) {
		this.byteArrayOutputStream = byteArrayOutputStream;
	}

	public void write(int param) throws IOException {
		byteArrayOutputStream.write(param);
	}
	
	private ByteArrayOutputStream byteArrayOutputStream;
}

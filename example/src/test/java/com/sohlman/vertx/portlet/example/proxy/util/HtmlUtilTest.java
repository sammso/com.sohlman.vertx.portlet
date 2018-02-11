package com.sohlman.vertx.portlet.example.proxy.util;

import org.junit.Assert;
import org.junit.Test;



public class HtmlUtilTest {
	
	
	@Test
	public void testBodyParse() {
		String html = "<html><head><title>my title</title></head><body><h1>hello world</h1></body></html>";
		Assert.assertEquals(HtmlUtil.getHtmlBody("body", html), "<h1>hello world</h1>");
	}
	@Test
	public void testHeadParse() {
		String html = "<html><head><title>my title</title></head><body><h1>hello world</h1></body></html>";
		Assert.assertEquals(HtmlUtil.getHtmlBody("head", html), "<title>my title</title>");
	}	
}

package com.sohlman.vertx.portlet.example.proxy.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlUtil {
	public static String getHtmlBody(String selector, String html) {
		Document document = Jsoup.parse(html);
		return document.select(selector).html();
	}
}

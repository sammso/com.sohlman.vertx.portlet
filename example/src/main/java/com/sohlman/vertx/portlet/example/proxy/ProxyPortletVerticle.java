package com.sohlman.vertx.portlet.example.proxy;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.Http;
import com.sohlman.vertx.portlet.api.PortletVerticle;
import com.sohlman.vertx.portlet.api.RenderRequest;
import com.sohlman.vertx.portlet.example.proxy.constants.PortletKeys;
import com.sohlman.vertx.portlet.example.proxy.util.HtmlUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.vertx.core.Promise;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

@Component(immediate = true, property = { 
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Proxy Portlet", 
		"javax.portlet.name=" + PortletKeys.PROXY_PORTLET,
		"javax.portlet.security-role-ref=power-user,user" 
		}, service = PortletVerticle.class)
public class ProxyPortletVerticle extends PortletVerticle {

	@Override
	public void render(RenderRequest renderRequest, Promise<String> responsePromise) {
		WebClient client = WebClient.create(vertx);

		String url = renderRequest.getPreferencesValue("url", "");
		String selector = renderRequest.getPreferencesValue("selector", "body");

		if ("".contentEquals(url)) {
			responsePromise.complete(
					"<h1>Portlet is not configured</h1><p>Please configure portlet from configuration menu</p>");
		} else {
			client.getAbs(url).as(BodyCodec.string()).send(asyncResult -> {

				if (asyncResult.succeeded()) {
					HttpResponse<String> response = asyncResult.result();

					responsePromise.complete(HtmlUtil.getHtmlBody(selector, response.body()));
				} else {
					responsePromise.complete(String.format("<h1>Error</h1><p>%s</p><p>URL: %s</p>",
							_html.escape(asyncResult.cause().getMessage()), url));
				}
			});
		}
	}

	@Reference
	private Http _http;

	@Reference
	private Html _html;

	private Log _log = LogFactoryUtil.getLog(ProxyPortletVerticle.class);
}

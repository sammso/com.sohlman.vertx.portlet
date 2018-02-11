package com.sohlman.vertx.portlet.example.slow;

import com.liferay.portal.kernel.util.Portal;
import com.sohlman.vertx.portlet.api.PortletVerticle;
import com.sohlman.vertx.portlet.api.RenderRequest;

import java.util.Random;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.vertx.core.Promise;

@Component(
		immediate = true,
		property = {
			"com.liferay.portlet.instanceable=true",
			"javax.portlet.display-name=Slow Portlet",
			"javax.portlet.name=vertx-portlet",
			"javax.portlet.security-role-ref=power-user,user"
		},
		service = PortletVerticle.class
	)
public class ExamplePortletVerticle extends PortletVerticle {

	@Override
	public void render(RenderRequest renderRequest, Promise<String> responsePromise) {
		Random r = new Random();
		
		long timer = r.nextInt(900-500) + 500;
		
		long start = System.currentTimeMillis();
		
		vertx.setTimer(timer, id -> {
			responsePromise.complete("Render x time: " + (System.currentTimeMillis() - start));
		});
	}

	@Reference
	private Portal _portal;
}

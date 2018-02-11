package com.sohlman.vertx.portlet.api;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;


/**
 * @author Sampsa Sohlman <sampsa@sohlman.com>
 */
public class PortletVerticle extends AbstractVerticle {
	@SuppressWarnings("deprecation")
	public void processAction(
			Promise<Void> promise, 
			ActionRequest request, 
			ActionResponse response) {
		promise.complete();
	}
	
	public void render(RenderRequest renderRequest, Promise<String> response) {
		response.complete("Render is not implemented");
	}	
}

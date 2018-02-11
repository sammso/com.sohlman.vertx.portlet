package com.sohlman.vertx.portlet.liferay.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.sohlman.vertx.portlet.api.PortletVerticle;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class VertxPortletLiferay extends GenericPortlet {
	public VertxPortletLiferay(PortletVerticle portletVerticle, Vertx vertx, Portal portal) {
		this.portletVerticle = portletVerticle;
		this.vertx = vertx;
		this.portal = portal;
		
	}
	
	@Override
	public void init() throws PortletException {				
		vertx.deployVerticle(portletVerticle, deploymentId -> {
			deployumentId = deploymentId.result();
		});
		
		super.init();
	}
	
	@Override
	public void destroy() {
		vertx.undeploy(deployumentId);
		super.destroy();
	}
	
	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		portletVerticle.getVertx().getOrCreateContext().runOnContext(action -> {
			Promise<Void> actionPromise = Promise.promise();
			portletVerticle.processAction(actionPromise, request, response);
			
			actionPromise.future().setHandler(handler -> {
				countDownLatch.countDown();
			});
		});
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// Ignore
			log.warn(e);
		}
		
	}
	
	
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {
		HttpServletRequest httpServletRequest = 
			portal.getHttpServletRequest(renderRequest);
		httpServletRequest = portal.getOriginalServletRequest(httpServletRequest);
		ResultQueue resultQueue = (ResultQueue)httpServletRequest.getAttribute(ResultQueue.class.getName());
		
		if (resultQueue!=null) {
			//
			// Generate Key for replaceFilter
			// 
			String key = renderResponse.getNamespace() + counter.getAndIncrement();
			
			com.sohlman.vertx.portlet.liferay.internal.LiferayRenderRequest vertxRenderRequest = 
				new com.sohlman.vertx.portlet.liferay.internal.LiferayRenderRequest(renderResponse.getNamespace(), renderRequest);
 			
			// Prepare queue to use key
			// This is also making sure that queue is waiting at Filter
			resultQueue.prepare(key);
			
			portletVerticle.getVertx().getOrCreateContext().runOnContext(action -> {
				//
				// Here we are at Verx event loop
				//
				
				if ( log.isDebugEnabled()) log.debug("runOnContext " + vertxRenderRequest.getNameSpace());
								
				Promise<String> responsePromise = Promise.promise();
				
				portletVerticle.render(vertxRenderRequest, responsePromise);
				
				responsePromise.future().setHandler(handler -> {
					if ( log.isDebugEnabled()) log.debug("future.setHandler " + vertxRenderRequest.getNameSpace());
					if (handler.succeeded()) {
						
						resultQueue.addResult(key, handler.result());
					}
					else {
						resultQueue.addResult(key, "FAILED");
					}
				});
			});
			
			// Render key to portlet and that be replaced at ReplaceFilter
			renderResponse.getWriter().println(Constants.KEY_START + key + Constants.KEY_END);			
		}
		else {
			renderResponse.getWriter().println("<b>Filter is not working</b>");
		}
	}
	
	private Portal portal;
	
	private AtomicLong counter = new AtomicLong(0);
	
	private Vertx vertx;
	private PortletVerticle portletVerticle;
	
	private volatile String deployumentId = null;
	
	private Log log = LogFactoryUtil.getLog(VertxPortletLiferay.class);
}
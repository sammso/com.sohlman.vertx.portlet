package com.sohlman.vertx.portlet.liferay.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.sohlman.vertx.portlet.api.PortletVerticle;

import io.vertx.core.Vertx;

@Component(immediate = true)
public class VertxPortletProvider {
	@Activate
	void activate(ComponentContext componentContext) {
		try {
			BundleContext bundleContext = componentContext.getBundleContext();

			Filter filter = bundleContext.createFilter(
				String.format("(objectClass=%s)", PortletVerticle.class.getName()));
			
			_portletVerticleServiceTrackerCustomizer = new PortletVerticleServiceTrackerCustomizer(vertx, portal);
			
			_serviceTracker  = new ServiceTracker<PortletVerticle, ServiceObjects<PortletVerticle>>(
					bundleContext, filter, _portletVerticleServiceTrackerCustomizer);
			
			_serviceTracker.open();

			if (_log.isInfoEnabled()) {
				_log.info("VertxPortletProvider activated");
			}
		} 
		catch (InvalidSyntaxException e) {
			_log.error(e);
		}
	}
	
	@Deactivate
	void deactivate() {	
		if (_serviceTracker!=null) {
			_serviceTracker.close();
			_portletVerticleServiceTrackerCustomizer.cleanPortletRegistrations();
			_portletVerticleServiceTrackerCustomizer=null;
		}
		
		if (_log.isInfoEnabled()) {
			_log.info("VertxPortletProvider deactivated");
		}		
	}
	
	@Reference
	private Vertx vertx;
	
	@Reference
	private Portal portal;
	
	private PortletVerticleServiceTrackerCustomizer _portletVerticleServiceTrackerCustomizer;
	private Log _log = LogFactoryUtil.getLog(VertxPortletProvider.class);
	private ServiceTracker<PortletVerticle, ServiceObjects<PortletVerticle>> _serviceTracker;
}

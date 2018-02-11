package com.sohlman.vertx.portlet.liferay.internal;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.Portlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.sohlman.vertx.portlet.api.PortletVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

public class PortletVerticleServiceTrackerCustomizer implements ServiceTrackerCustomizer<PortletVerticle, ServiceObjects<PortletVerticle>> { 

	public PortletVerticleServiceTrackerCustomizer(Vertx vertx, Portal portal) {
		this.vertx = vertx;
		this.portal = portal;
	}
	
	@Override
	public ServiceObjects<PortletVerticle> addingService(
			ServiceReference<PortletVerticle> serviceReference) {

		Bundle bundle = serviceReference.getBundle();
		BundleContext bundleContext = bundle.getBundleContext();
		
		ServiceObjects<PortletVerticle> serviceObjects = 
			bundleContext.getServiceObjects(serviceReference);
		
		PortletVerticle portletVerticle = serviceObjects.getService();
		
		Dictionary<String, Object> properties = createProperties(serviceReference);

		copyProperty(
			properties, 
			"com.liferay.portlet.display-category", "category.vertx");
	
		copyProperty(
			properties,
			"javax.portlet.name", portletVerticle.getClass().getName());
	
		copyProperty(
			properties, "javax.portlet.display-name", 
			portletVerticle.getClass().getName());
		
		copyProperty(
			properties, "javax.portlet.security-role-ref",
			new String[] {"power-user", "user"});
	
		ServiceRegistration<Portlet> serviceRegistration = 
			bundleContext.registerService(
				Portlet.class, new VertxPortletLiferay(portletVerticle, this.vertx, this.portal), 
				properties);

		_portletServiceRegistration.put(
			serviceReference, serviceRegistration);
		
		return serviceObjects;
	}
	@Override
	public void modifiedService(ServiceReference<PortletVerticle> serviceReference, 
			ServiceObjects<PortletVerticle> ui) {
		
		_log.info("modifiedService");

	}

	@Override
	public void removedService(ServiceReference<PortletVerticle> serviceReference, 
			ServiceObjects<PortletVerticle> portletVerticle) {

		ServiceRegistration<Portlet> serviceRegistration = 
			_portletServiceRegistration.get(serviceReference);

		_portletServiceRegistration.remove(serviceReference);	
		
		serviceRegistration.unregister();
		if (_log.isInfoEnabled()) {
			_log.info(
				"Vertx portlet unregistered for " + portletVerticle.getClass().getName() + 
				" left: " + _portletServiceRegistration.size());
		}
	}
	
	public void cleanPortletRegistrations() {
		
		Collection<ServiceRegistration<Portlet>> portletRegistrations = 
			_portletServiceRegistration.values();
		
		if (_log.isDebugEnabled()) {
			_log.debug("cleanPortletRegistrations count: " + 
				portletRegistrations.size());
		}
		
		Iterator<ServiceRegistration<Portlet>> iterator = 
			portletRegistrations.iterator();
		
		while(iterator.hasNext()) {
			iterator.next().unregister();
		}
	
		_portletServiceRegistration.clear();
		_portletServiceRegistration = null;
	}
	
	protected Dictionary<String, Object> createProperties(ServiceReference<PortletVerticle> serviceReference) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		
		
		for ( String key : serviceReference.getPropertyKeys()) {
			properties.put(key, serviceReference.getProperty(key));
		}
		return properties;
	}
	
	protected void copyProperty( 
			Dictionary<String, Object> properties, String key,
			Object defaultValue) {
		
		if ( properties.get(key)==null ) {
			properties.put(key, defaultValue);
		}
	}		
	
	private Map<ServiceReference<PortletVerticle>, ServiceRegistration<Portlet>> 
		_portletServiceRegistration = 
			new HashMap<ServiceReference<PortletVerticle>, ServiceRegistration<Portlet>>();
	
	private Log _log = 
		LogFactoryUtil.getLog(PortletVerticleServiceTrackerCustomizer.class);
	
	@Reference
	private Vertx vertx;
	
	@Reference
	private Portal portal;
}

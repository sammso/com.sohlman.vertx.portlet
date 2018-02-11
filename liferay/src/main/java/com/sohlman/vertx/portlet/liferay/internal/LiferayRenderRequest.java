package com.sohlman.vertx.portlet.liferay.internal;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.PortalContext;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

import com.sohlman.vertx.portlet.api.RenderRequest;


/**
 * 
 * Vert.x portlet RenderRequest
 * 
 * @author Sampsa Sohlman <sampsa@sohlman.com>
 */
public class LiferayRenderRequest implements RenderRequest {
	public LiferayRenderRequest(String nameSpace, javax.portlet.RenderRequest renderRequest) {
		this.portalContext = renderRequest.getPortalContext();
		this.cookies = renderRequest.getCookies();
		this.contextPath = renderRequest.getContextPath();
		this.parameterMap = new HashMap<>();
		this.nameSpace = nameSpace;
		this.preferencesMap = renderRequest.getPreferences().getMap();
		
		// Clone the the 
		for(Entry<String, String[]> entry : renderRequest.getParameterMap().entrySet()) {
			this.parameterMap.put(entry.getKey(), entry.getValue());
		}
		
		this.attributeMap = new HashMap<>();
		
		Enumeration<String> attributeNames = renderRequest.getAttributeNames();
		
		while(attributeNames.hasMoreElements()) {
			String name = attributeNames.nextElement();
			attributeMap.put(name, renderRequest.getAttribute(name));
		}
		
		this.windowState = renderRequest.getWindowState();
		
		this.locale = renderRequest.getLocale();
		
		this.localeList = new ArrayList<>();
		
		Enumeration<Locale> localeEnumeration = renderRequest.getLocales();
		
		while(localeEnumeration.hasMoreElements()) {
			localeList.add(localeEnumeration.nextElement());
		}
	}

	public String getAuthType() {
		return this.authType;
	}

	public String getContextPath() {
		return this.contextPath;	
	}

	public Cookie[] getCookies() {
		return this.cookies;
	}

	public Object getAttribute(String name) {
		return this.attributeMap.get(name);
	}

	public Set<String> getAttributeNames() {
		return this.attributeMap.keySet();
	}

	public Locale getLocale() {
		return this.locale;
	}

	public List<Locale> getLocales() {
		return this.localeList;
	}

	public String getNameSpace() {
		return this.nameSpace;
	}

	public String getParameter(String name) {
		String[] values = this.parameterMap.get(name);
		
		if (values!=null && values.length > 0) {
			return values[0];
		}
		else {
			return null;
		}
	}

	public Set<String> getParameterNames() {
		return this.parameterMap.keySet();
	}

	public String[] getParameterValues(String name) {
		return this.parameterMap.get(name);
	}

	public Map<String, String[]> getParameterMap() {
		return this.parameterMap;
	}

	public PortalContext getPortalContext() {
		return this.portalContext;
	}
	

	@Override
	public String[] getPreferencesValues(String key, String[] def) {
		String[] value = this.preferencesMap.get(key);
		
		if ( value==null ) {
			return def;
		}
		else {
			return value;
		}
	}

	@Override
	public String getPreferencesValue(String key, String def) {
		String[] value = this.preferencesMap.get(key);
		
		if ( value==null || value.length==0 ) {
			return def;
		}
		else {
			return value[0];
		}
	}	
	
	public WindowState getWindowState() {
		return this.windowState;
	}
	
	private String authType;

	private Map<String, Object> attributeMap;
	
	
	
	private String contextPath;
	
	private Cookie[] cookies;
	
	private Locale locale;
	
	private List<Locale> localeList;
	
	private Map<String, String[]> parameterMap;
	
	private Map<String, String[]> preferencesMap;
	
	private PortalContext portalContext;
	
	private String nameSpace;
	
	private WindowState windowState;
}

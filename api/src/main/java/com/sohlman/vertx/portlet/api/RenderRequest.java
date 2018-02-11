package com.sohlman.vertx.portlet.api;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortalContext;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

public interface RenderRequest {
	public String getAuthType();

	public String getContextPath();

	public Cookie[] getCookies();

	public Object getAttribute(String name);

	public Set<String> getAttributeNames();

	public Locale getLocale();

	public List<Locale> getLocales();

	public String getNameSpace();

	public String getParameter(String name);

	public Set<String> getParameterNames();

	public String[] getParameterValues(String name);

	public Map<String, String[]> getParameterMap();

	public String[] getPreferencesValues(String key, String[] def);
	
	public String getPreferencesValue(String key, String def);	
	
	public PortalContext getPortalContext();
	
	public WindowState getWindowState();
}

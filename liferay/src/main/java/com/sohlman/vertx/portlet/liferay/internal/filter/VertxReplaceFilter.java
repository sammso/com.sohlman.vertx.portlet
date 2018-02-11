package com.sohlman.vertx.portlet.liferay.internal.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.sohlman.vertx.portlet.liferay.internal.Constants;
import com.sohlman.vertx.portlet.liferay.internal.ResultQueue;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(
	immediate = true, 
	property = { 
		"before-filter=Cache Filter - Friendly", 
		"dispatcher=FORWARD",
		"dispatcher=REQUEST", 
		"servlet-context-name=", 
		"servlet-filter-name=Vertx Filter",
		"url-pattern=/*" 
	}, 
	service = Filter.class)
public class VertxReplaceFilter extends BaseFilter {
	@Override
	public void destroy() {
	}

	@Override
	protected void processFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws Exception {

		long companyId = this.portal.getCompanyId(request);
		if (isLayout(companyId, request)) {
			long start = System.currentTimeMillis();
			ResultQueue resultQueue = new ResultQueue();

			request.setAttribute(ResultQueue.class.getName(), resultQueue);

			ByteArrayResponseWrapper responseWrapper = new ByteArrayResponseWrapper(response);

			processFilter(VertxReplaceFilter.class.getName(), request, responseWrapper, filterChain);

			String servletResponse = new String(responseWrapper.toString());
			
			servletResponse = StringUtil.replace(servletResponse, Constants.KEY_START,Constants.KEY_END, resultQueue.getResult());

			response.getWriter().write(servletResponse);
			
			if (log.isDebugEnabled()) {
				log.debug("Page rendering time: ".concat(String.valueOf(System.currentTimeMillis() - start)).concat(" ms"));
			}
		} else {
			processFilter(VertxReplaceFilter.class.getName(), request, response, filterChain);
		}
	}

	protected boolean isLayout(long companyId, HttpServletRequest request) {
		// Method logic copied from
		// com.liferay.portal.servlet.filters.cache.CacheFilter.isCacheableData(..)
		try {

			long plid = ParamUtil.getLong(request, "p_l_id");

			if (plid <= 0) {
				return false;
			}

			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			if (!layout.isTypePortlet()) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected Log getLog() {
		return log;
	}

	@Reference
	private Portal portal;

	private Log log = LogFactoryUtil.getLog(VertxReplaceFilter.class);
}

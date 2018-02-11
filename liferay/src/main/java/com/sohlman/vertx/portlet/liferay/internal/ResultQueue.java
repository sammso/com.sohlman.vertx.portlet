package com.sohlman.vertx.portlet.liferay.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ResultQueue {
	public ResultQueue() {
		
	}
	
	public void prepare(String key) {
		cauntUpAndDownLatch.increment();
		replaceMap.put(key,"");
	}
	
	public void addResult(String key, String value) {
		replaceMap.put(key, value);
		cauntUpAndDownLatch.countDown();
	}
	
	public Map<String, String> getResult() {
		try {
			cauntUpAndDownLatch.await(30L, TimeUnit.SECONDS);
			//cauntUpAndDownLatch.await();
		}
		catch(InterruptedException ie) {
			// Ignore
			log.error(ie.getMessage());
		}
		
		return new HashMap<>(replaceMap); // Clone
	}
	
	
	private CountUpAndDownLatch cauntUpAndDownLatch = new CountUpAndDownLatch();
	private Map<String, String> replaceMap = new ConcurrentHashMap<>();
	private String key = null;
	/**
	 * This object is not cloneable. This returns itself (this).
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return this;
	}

	private Log log = LogFactoryUtil.getLog(ResultQueue.class);
}
	


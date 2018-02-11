package com.sohlman.vertx.portlet.liferay.internal;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

@Component(immediate = true)
public class VertxProvider {
	@Activate
	void activate(ComponentContext componentContext) {
		vertx = Vertx.vertx();
		BundleContext bundleContext = componentContext.getBundleContext();
		vertxServiceRegistration = bundleContext.registerService(Vertx.class, vertx, new Hashtable<String, Object>());
		
		workerExecutor = vertx.createSharedWorkerExecutor("portletVerticleExecutor");
		workerExecutorServiceRegistration = bundleContext.registerService(WorkerExecutor.class, this.workerExecutor, new Hashtable<String, Object>());
		
		log.info("Vertx registration");
	}

	@Deactivate
	void deactivate() {
		workerExecutorServiceRegistration.unregister();
		workerExecutor.close();
		workerExecutor=null;
		vertxServiceRegistration.unregister();
		vertx.close();
		vertx = null;
		log.info("Vertx unregister");
	}

	private ServiceRegistration<Vertx> vertxServiceRegistration;
	private ServiceRegistration<WorkerExecutor> workerExecutorServiceRegistration;
	
	private Log log = LogFactoryUtil.getLog(VertxProvider.class);
	private Vertx vertx;
	private WorkerExecutor workerExecutor;
}

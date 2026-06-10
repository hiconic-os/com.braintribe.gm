package com.braintribe.logging.level.test;
import java.io.File;
import java.util.concurrent.Executors;

import com.braintribe.gm.logging.level.LogLevelServiceProcessor;
import com.braintribe.logging.level.DirectLogLevelUpdateDispatcher;
import com.braintribe.logging.level.LogLevelSetup;
import com.braintribe.logging.level.LogLevelManager;
import com.braintribe.logging.level.LogLevelRuntimeUpdater;
import com.braintribe.logging.level.servlet.LogLevelServlet;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.common.eval.ConfigurableServiceRequestEvaluator;
import com.braintribe.model.service.api.ServiceRequest;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;

public class LogLevelServletLab {
    public static void main(String[] args) throws Exception {
        File confDir = new File("res");

        LogLevelServlet servlet = new LogLevelServlet();
        LogLevelSetup setup = new LogLevelSetup(confDir, LogLevelServletLab::property);

        LogLevelSetup.setInstance(setup);

        LogLevelManager logLevelManager = LogLevelSetup.instance().logLevelManager();
        DirectLogLevelUpdateDispatcher updateDispatcher = new DirectLogLevelUpdateDispatcher();
        LogLevelRuntimeUpdater runtimeUpdater = new LogLevelRuntimeUpdater();

		LogLevelSetup.instance().applyEffectiveLogLevels();
		updateDispatcher.setLogLevelManager(logLevelManager);
		runtimeUpdater.setLogLevelManager(logLevelManager);
		runtimeUpdater.setUpdateDispatcher(updateDispatcher);
		servlet.setEvaluator(evaluator(logLevelManager, runtimeUpdater));

		InstanceFactory<LogLevelServlet> immediateInstanceHandle = new ImmediateInstanceFactory<LogLevelServlet>(servlet);
		
        ServletInfo myServlet = Servlets.servlet("log-levels", LogLevelServlet.class, immediateInstanceHandle)
                .addMapping("/log-levels");

        DeploymentInfo deployment = Servlets.deployment()
                .setClassLoader(LogLevelServletLab.class.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("log-levels.war")
                .addServlets(myServlet);

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(deployment);
        manager.deploy();

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(manager.start())
                .build();

        server.start();
        System.out.println("Server started on http://localhost:8080/log-levels");
        System.out.println("Configuration directory: " + confDir.getAbsolutePath());
    }

	private static String property(String name) {
		String value = System.getProperty(name);

		return value != null ? value : System.getenv(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ConfigurableServiceRequestEvaluator evaluator(LogLevelManager logLevelManager, LogLevelRuntimeUpdater runtimeUpdater) {
		LogLevelServiceProcessor processor = new LogLevelServiceProcessor();
		processor.setLogLevelManager(logLevelManager);
		processor.setLogLevelRuntimeUpdater(runtimeUpdater);

		ConfigurableServiceRequestEvaluator evaluator = new ConfigurableServiceRequestEvaluator();
		evaluator.setServiceProcessor((ServiceProcessor<ServiceRequest, Object>) (ServiceProcessor) processor);
		evaluator.setExecutorService(Executors.newCachedThreadPool());
		return evaluator;
	}
}

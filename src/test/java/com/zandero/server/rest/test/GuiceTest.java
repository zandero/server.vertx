package com.zandero.server.rest.test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intapp.vertx.guice.GuiceVerticleFactory;
import com.intapp.vertx.guice.GuiceVertxDeploymentManager;
import com.zandero.server.RestVerticle;
import com.zandero.server.guice.BaseRestModule;
import com.zandero.settings.Settings;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class GuiceTest {

	protected static final int PORT = 4444;

	protected static final String HOST = "localhost";

	protected List<AbstractModule> testModules;

	protected Vertx vertx;

	protected HttpClient client;

	public GuiceTest() {

		testModules = new ArrayList<>();
		testModules.add(new org.jboss.resteasy.plugins.guice.ext.RequestScopeModule());

		Settings settings = new Settings();
		settings.put("port", PORT);

		testModules.add(new BaseRestModule(settings));
	}

	List<AbstractModule> getModules() {

		return testModules;
	}

	@Before
	public void before(TestContext context) {

		vertx = Vertx.vertx();
		//vertx.deployVerticle(RestVerticle.class.getName(), context.asyncAssertSuccess());

		Injector injector = Guice.createInjector(testModules);

		GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(injector);
		vertx.registerVerticleFactory(guiceVerticleFactory);

		GuiceVertxDeploymentManager deploymentManager = new GuiceVertxDeploymentManager(vertx);
		deploymentManager.deployVerticle(RestVerticle.class, new DeploymentOptions(), context.asyncAssertSuccess());

		client = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(HOST).setDefaultPort(PORT));
	}

	@After
	public void after(TestContext context) {

		vertx.close(context.asyncAssertSuccess());
	}
}

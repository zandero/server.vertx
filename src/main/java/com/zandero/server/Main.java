package com.zandero.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intapp.vertx.guice.GuiceVerticleFactory;
import com.intapp.vertx.guice.GuiceVertxDeploymentManager;
import com.zandero.cmd.CommandBuilder;
import com.zandero.cmd.CommandLineParser;
import com.zandero.cmd.option.CommandOption;
import com.zandero.cmd.option.IntOption;
import com.zandero.server.guice.BaseRestModule;
import com.zandero.settings.Settings;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty set up with Guice and RestEasy
 */
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {

		CommandOption port = new IntOption("p")
			.longCommand("port")
			.setting("port")
			.defautlTo(4444);

		CommandBuilder builder = new CommandBuilder();
		builder.add(port);

		CommandLineParser parser = new CommandLineParser(builder);
		Settings settings = parser.parse(args);

		Injector injector = Guice.createInjector(
			new BaseRestModule(settings));

		try {

			ClusterManager mgr = new HazelcastClusterManager();
			VertxOptions options = new VertxOptions()
				.setClusterManager(mgr)
				.setMaxEventLoopExecuteTime(Long.MAX_VALUE);

			Vertx.clusteredVertx(options, res -> {
				if (res.succeeded()) {
					Vertx vertx = res.result();

					GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(injector);
					vertx.registerVerticleFactory(guiceVerticleFactory);

					GuiceVertxDeploymentManager deploymentManager = new GuiceVertxDeploymentManager(vertx);
					DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true);

					deploymentManager.deployVerticle(RestVerticle.class, deploymentOptions);
					deploymentManager.deployVerticle(TestVerticle.class, deploymentOptions);
				}
				else {
					log.error("Could not deploy verticle, reason : " + res.failed());
				}
			});
		}
		catch (Exception e) {

			log.error("Unhandled exception: ", e);
			System.out.println(e.getMessage());
		}
	}
}

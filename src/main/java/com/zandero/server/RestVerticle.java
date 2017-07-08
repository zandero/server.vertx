package com.zandero.server;

import com.zandero.rest.RestRouter;
import com.zandero.server.entities.UserRole;
import com.zandero.server.rest.*;
import com.zandero.server.service.SessionService;
import com.zandero.settings.Settings;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Takes care of server initialization ...
 * binds all needed handlers ...
 */
public class RestVerticle extends AbstractVerticle {

	private final static Logger log = LoggerFactory.getLogger(RestVerticle.class);

	private final Provider<Settings> settings;

	private final SessionService sessions;

	private final Handler<RoutingContext> echoHandler;

	private final Handler<RoutingContext> loginHandler;

	private final Handler<RoutingContext> infoHandler;

	private Handler<RoutingContext> globalExceptionHandler;

	@Inject
	public RestVerticle(EchoHandler echoRestHandler,
	                    LoginHandler loginRestHandler,
	                    InfoHandler infoRestHandler,
	                    SessionService sessionService,
	                    GlobalExceptionHandler globalRestExceptionHandler,
	                    Provider<Settings> settingsProvider) {


		sessions = sessionService;
		settings = settingsProvider;

		loginHandler = loginRestHandler;
		echoHandler = echoRestHandler;
		infoHandler = infoRestHandler;

		globalExceptionHandler = globalRestExceptionHandler;
	}

	@Override
	public void start() throws Exception {

		final RequestContextHandler contextHandler = new RequestContextHandler(sessions);

		// add authorization filter ...

		Router router = Router.router(vertx);
		router.route().handler(contextHandler);
		router.route().handler(CookieHandler.create());

		router.get("/echo").handler(echoHandler);
		router.get("/login").handler(loginHandler);
		router.get("/info").handler(infoHandler);

		// secure
		router.get("/private").handler(new CheckRoleHandler(UserRole.Admin));
		router.get("/private").handler(infoHandler); // same handler but user must be an admin to get a result


		RestRouter.register(router, TestVerticle.class);

		// NOT WORKING: router.get("/private2").handler(new CheckRoleHandler(UserRole.Admin)).handler(infoHandler);

		// reg ex router
		router.getWithRegex("\\/\\d\\/\\d").handler(echoHandler);

		router.route().failureHandler(globalExceptionHandler);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(settings.get().getInt("port"));

		log.info(this.getClass().getName() + " is deployed successfully");
	}
}

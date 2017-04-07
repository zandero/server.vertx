package com.zandero.server.rest;

import com.zandero.server.entities.User;
import com.zandero.server.service.SessionService;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class RequestContextHandler implements Handler<RoutingContext> {

	private final static Logger log = LoggerFactory.getLogger(RequestContextHandler.class);

	public static final String SESSION_HEADER = "X-SessionId";

	private final SessionService sessions;

	public RequestContextHandler(SessionService sessionService) {

		sessions = sessionService;
	}

	@Override
	public void handle(RoutingContext context) {

		String sessionId = context.request().getHeader(SESSION_HEADER);

		/*authProvider.authenticate(authInfo,  res -> {
			if (res.succeeded()) {
				User authenticated = res.result();
				context.setUser(authenticated);
				authorise(authenticated, context);
			} else {
				handle401(context);
			}
		});*/

		User found = null;
		if (sessionId != null) {
			found = sessions.get(sessionId);
		}

		if (found != null) {
			log.info("User session found: " + found);
			context.setUser(found);
		}
		else {
			log.info("No user session found!");
		}

		// push RoutingContext into RestEasy
		//ResteasyProviderFactory.pushContext(RoutingContext.class, context);

		context.next();
	}
}

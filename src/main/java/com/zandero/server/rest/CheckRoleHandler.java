package com.zandero.server.rest;

import com.zandero.server.RestVerticle;
import com.zandero.server.entities.User;
import com.zandero.server.entities.UserRole;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CheckRoleHandler implements Handler<RoutingContext> {

	private final static Logger log = LoggerFactory.getLogger(CheckRoleHandler.class);

	private final UserRole accept;

	public CheckRoleHandler(UserRole role) {

		accept = role;
	}

	@Override
	public void handle(RoutingContext event) {

		log.info("Checking user role: " + accept);

		boolean allowed = false;
		if (event.user() instanceof User) {
			User user = (User) event.user();

			log.info(user.getUsername() + " is " + user.getRole());
			allowed = user.getRole().isAllowed(accept);
		}

		// quit here
		if (!allowed) {
			event.response()
				.setStatusCode(HttpStatus.SC_UNAUTHORIZED)
				.end();
		}
		else {
			event.next();
		}
	}
}

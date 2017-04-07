package com.zandero.server.rest;

import com.zandero.server.entities.User;
import com.zandero.server.entities.json.UserJSON;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;

/**
 *
 */
public class InfoHandler implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext event) {

		if (event.user() instanceof User) {
			// user logged in ... return JSON of user
			User user = (User) event.user();
			event.response()
				.putHeader("content-type", "application/json")
				.setStatusCode(200)
				.end(Json.encodePrettily(new UserJSON(user)));
		}
		else {
			event.response()
				.setStatusCode(HttpStatus.SC_NO_CONTENT)
				.end();
		}
	}
}

package com.zandero.server.rest;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 *
 */
public class EchoHandler implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext event) {

		event.response().setStatusCode(200).end("Hello World");
	}
}

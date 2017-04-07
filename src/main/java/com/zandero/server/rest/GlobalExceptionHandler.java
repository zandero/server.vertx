package com.zandero.server.rest;

import com.google.inject.ProvisionException;
import com.zandero.utils.JsonUtils;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.spi.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 */
public class GlobalExceptionHandler implements Handler<RoutingContext> {

	private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


	@Override
	public void handle(RoutingContext context) {

		Throwable exception = context.failure();

		try {
			if (exception == null) { // should not happen ...
				throw new IllegalArgumentException("Missing exception!");
			}
			// to avoid ugly casting, just throw exception and catch typed exception below
			throw exception;
		}
		catch (ProvisionException e) {
			log.error("ProvisionException error: ", e);
			// catch wrapped GUICE errors unwrap and call again or create response as found
			if (e.getCause() != null) {
				getResponse(context, HttpStatus.SC_BAD_REQUEST, e);
			}
			else {
				getResponse(context, HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
			}
		}
		catch (WebApplicationException e) {
			log.error("Web application excpetion: ", e);
			getResponse(context, e.getResponse().getStatus(), e);
		}
		catch (Failure e) {
			log.error("Failure: ", e);
			getResponse(context, e.getErrorCode(), e);
		}
		catch (IllegalArgumentException e) {
			log.error("Missing or invalid parameters: ", e);
			getResponse(context, HttpStatus.SC_NOT_ACCEPTABLE, e);
		}
		catch (Throwable e) {
			log.error("Application error: ", e);
			// other exceptions...
			getResponse(context, HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
		}
	}

	private void getResponse(RoutingContext context, int status, Throwable e) {

		context.response()
			.setStatusCode(status)
			.putHeader("Content-Type", MediaType.APPLICATION_JSON)
			.end(JsonUtils.toJson(new RestException(e, status)));
	}
}

package com.zandero.server;

import com.zandero.rest.RestRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/test")
public class TestVerticle { // extends AbstractVerticle {

	private final static Logger log = LoggerFactory.getLogger(RestVerticle.class);

	/*@Override
	public void start() throws Exception {

		// do annotation processing of the class implementing this verticle
		Router router = RestRouter.register(vertx, this);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(4444);

		log.info(this.getClass().getName() + " is deployed successfully");
	}*/

	@GET
	@Path("/call")
	@Consumes("application/json")
//	@Produces("application/json")
	public String testCall() {

		return "test";
	}

	@GET
	@Path("/call2")
	@Consumes("application/json")
	@Produces("application/json")
	public String testCall2() {

		return "test2";
	}

	@GET
	@Path("/call3")
	//@Consumes("application/json")
	@Produces("application/json")
	public Response testCall3() {

		return Response
			.accepted("Test")
			.header("X-Test", "test")
			.build();
	}
}
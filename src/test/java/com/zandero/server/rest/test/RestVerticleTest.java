package com.zandero.server.rest.test;

import com.zandero.http.HttpUtils;
import com.zandero.server.entities.UserRole;
import com.zandero.server.entities.json.UserJSON;
import com.zandero.server.rest.RequestContextHandler;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RestVerticleTest extends GuiceTest {

	@Test
	public void echoTest(TestContext context) {
		final Async async = context.async();

		client.getNow("/echo", response -> {

			context.assertEquals(HttpStatus.SC_OK, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Hello World", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void loginInfoTest(TestContext context) throws IOException {
		final Async async = context.async();


		HttpRequestBase req = HttpUtils.get("http://localhost:4444/info");
		HttpResponse response = HttpUtils.execute(req);

		context.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());

		// #2.
		req = HttpUtils.get("http://localhost:4444/login?username=user&password=password");
		response = HttpUtils.execute(req);

		context.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

		String token = response.getHeaders(RequestContextHandler.SESSION_HEADER)[0].getValue();
		context.assertNotNull(token);

		String content = HttpUtils.getContentAsString(response);

		UserJSON user = JsonUtils.fromJson(content, UserJSON.class);
		context.assertEquals("Some User", user.fullName);
		context.assertEquals("user", user.username);
		context.assertEquals(UserRole.User, user.role);

		// #2.
		req = HttpUtils.get("http://localhost:4444/info");
		req.setHeader(RequestContextHandler.SESSION_HEADER, token);
		response = HttpUtils.execute(req);

		context.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		content = HttpUtils.getContentAsString(response);

		user = JsonUtils.fromJson(content, UserJSON.class);
		context.assertEquals("Some User", user.fullName);
		context.assertEquals("user", user.username);
		context.assertEquals(UserRole.User, user.role);

		// #3. try private
		req = HttpUtils.get("http://localhost:4444/private");
		req.setHeader(RequestContextHandler.SESSION_HEADER, token);
		response = HttpUtils.execute(req);

		context.assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());

		async.complete();
	}

	@Test
	public void privateAccessDeniedTest(TestContext context) {
		final Async async = context.async();

		vertx.createHttpClient().getNow(PORT, HOST, "/private", response -> {

			context.assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void privateAccessTest(TestContext context) throws IOException {
		final Async async = context.async();

		// #2.
		HttpRequestBase req = HttpUtils.get("http://localhost:4444/login?username=admin&password=password");
		HttpResponse response = HttpUtils.execute(req);

		context.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

		String token = response.getHeaders(RequestContextHandler.SESSION_HEADER)[0].getValue();
		context.assertNotNull(token);

		// #3. try private
		req = HttpUtils.get("http://localhost:4444/private");
		req.setHeader(RequestContextHandler.SESSION_HEADER, token);
		response = HttpUtils.execute(req);
		context.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

		async.complete();
	}

	@Test
	public void exceptionHandlerTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/login", response -> {

			context.assertEquals(HttpStatus.SC_NOT_ACCEPTABLE, response.statusCode());

			response.handler(body -> {
				context.assertEquals("{\"code\":406,\"message\":\"Missing username and password!\"}", body.toString());
				async.complete();
			});
		});
	}
}

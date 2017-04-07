package com.zandero.server.rest;

import com.zandero.server.entities.User;
import com.zandero.server.service.SessionService;
import com.zandero.utils.Assert;
import com.zandero.utils.JsonUtils;
import com.zandero.utils.UrlUtils;
import io.vertx.core.Handler;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;

import javax.inject.Inject;
import java.util.Map;

/**
 *
 */
public class LoginHandler implements Handler<RoutingContext> {

	private final SessionService sessions;

	@Inject
	public LoginHandler(SessionService sessionService) {

		sessions = sessionService;
	}

	@Override
	public void handle(RoutingContext event) {

		Assert.notNull(event.request().query(), "Missing username and password!");

		Map<String, String> query = UrlUtils.getQuery(event.request().query());

		String username = query.get("username");
		String password = query.get("password");

		String token = sessions.login(username, password);
		if (token != null) {

			/*return Response.ok(new UserJSON(user).setToken(sessionId))
				.header(BackendRequestContext.SESSION_HEADER, sessionId)
				.type(MediaType.APPLICATION_JSON)
				.cookie(new NewCookie(BackendRequestContext.SESSION_HEADER, sessionId))
				.build();*/

		/*	long age = 158132000l; //5 years in seconds
			Cookie cookie = Cookie.cookie(RequestContextHandler.SESSION_HEADER, token);
			String path = "/"; //give any suitable path
			cookie.setPath(path);
			cookie.setMaxAge(age); //if this is not there, then a session cookie is set
			event.addCookie(cookie);

			event.response().setChunked(true);
			event.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			event.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET");*/
			//context.response().write("Cookie Stamped -> " + fullName + " : " +value);

			User user = sessions.get(token);

			event.addCookie(Cookie.cookie(RequestContextHandler.SESSION_HEADER, token).setPath("/"))
				.response()
				.setStatusCode(HttpStatus.SC_OK)
				.putHeader(RequestContextHandler.SESSION_HEADER, token)
				.end(JsonUtils.toJson(user));
		}
		else {
			// else throw error
			event.response().setStatusCode(HttpStatus.SC_UNAUTHORIZED).end();
		}
	}
}

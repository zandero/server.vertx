package com.zandero.server.entities;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy user entity
 */
public class User extends AbstractUser {

	private final static Logger log = LoggerFactory.getLogger(User.class);

	private final String username;

	private final String password;

	private final String fullName;

	private final UserRole role;

	public User(String username, String password, String fullName, UserRole role) {

		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.role = role;
	}

	public String getUsername() {

		return username;
	}

	public String getPassword() {

		return password;
	}

	public String getFullName() {

		return fullName;
	}

	public UserRole getRole() {

		return role;
	}

	@Override
	protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {

	}

	@Override
	public JsonObject principal() {

		return null;
	}

	@Override
	public void setAuthProvider(AuthProvider authProvider) {

	}
}

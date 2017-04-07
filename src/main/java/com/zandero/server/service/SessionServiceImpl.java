package com.zandero.server.service;

import com.zandero.server.entities.User;
import com.zandero.server.entities.UserRole;
import com.zandero.utils.Assert;
import com.zandero.utils.KeyGenerator;

import java.util.*;

/**
 * Simple session service with 2 static users
 */
public class SessionServiceImpl implements SessionService {

	// configure static user storage
	private static List<User> users;
	{
		users = new ArrayList<>();
		users.add(new User("admin", "password", "The Admin", UserRole.Admin));
		users.add(new User("user", "password", "Some User", UserRole.User));
	}

	private static Map<String, String> sessions = new HashMap<>();

	@Override
	public String login(String username, String password) {

		Assert.notNullOrEmptyTrimmed(username, "Missing 'username'");
		Assert.notNullOrEmptyTrimmed(password, "Missing 'password'");

		User found = getUser(username);

		if (found != null) {

			if (found.getPassword().equals(password)) {

				String session = KeyGenerator.generateString(10);
				sessions.put(session, username);
				return session;
			}
		}

		throw new SecurityException("Invalid username or password");
	}

	private User getUser(String username) {

		Optional<User> found = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
		return found.orElse(null);
	}

	@Override
	public User get(String sessionId) {

		Assert.notNullOrEmptyTrimmed(sessionId, "Missing session id!");

		String username = sessions.get(sessionId);
		return getUser(username);
	}
}

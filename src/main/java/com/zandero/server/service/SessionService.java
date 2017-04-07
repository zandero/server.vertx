package com.zandero.server.service;

import com.zandero.server.entities.User;

/**
 *
 */
public interface SessionService  {

	/**
	 * Logs in user and return session id
	 * @param username user fullName
	 * @param password user password
	 * @return new session id
	 */
	String login(String username, String password);

	/**
	 * Find user by session id
	 * @param sessionId session id
	 * @return exsiting user by session id, null if session not found
	 */
	User get(String sessionId);
}

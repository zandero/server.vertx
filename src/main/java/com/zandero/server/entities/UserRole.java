package com.zandero.server.entities;

import com.zandero.utils.StringUtils;

/**
 *
 */
public enum UserRole {

	User(0),
	Admin(10); // Admin is also a User ...

	/**
	 * Level defines privilege order ...
	 * the higher the more privileges user has
	 */
	private final int userLevel;

	UserRole(int level) {

		userLevel = level;
	}

	public static UserRole parse(String role) {

		if (StringUtils.isNullOrEmptyTrimmed(role)) {
			return null;
		}

		for (UserRole item: UserRole.values()) {

			if (StringUtils.equals(item.name(), role, true)) {
				return item;
			}
		}

		return null;
	}

	public boolean isAllowed(UserRole role) {

		return this.userLevel >= role.userLevel;
	}
}

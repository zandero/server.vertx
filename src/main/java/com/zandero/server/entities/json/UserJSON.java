package com.zandero.server.entities.json;

//import com.zandero.rest.annotations.NotNullAndIgnoreUnknowns;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zandero.server.entities.User;
import com.zandero.server.entities.UserRole;

/**
 * JSON representation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserJSON {

	public String fullName;

	public String username;

	public UserRole role;

	public String token;

	private UserJSON() {}

	public UserJSON(User user) {

		fullName = user.getFullName();
		username = user.getUsername();
		role = user.getRole();
	}

	public UserJSON setToken(String value) {
		token = value;
		return this;
	}
}

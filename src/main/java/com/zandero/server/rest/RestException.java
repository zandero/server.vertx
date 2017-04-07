package com.zandero.server.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zandero.utils.Assert;
import com.zandero.utils.JsonUtils;
import org.jboss.resteasy.spi.Failure;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;

/**
 * Base class to wrap exceptions into JSON structure
 * Extend this class in order to provide additional info when throwing exceptions
 */
@JsonIgnoreProperties(
	value = {"stackTrace", "localizedMessage", "suppressed"},
	ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestException extends Exception implements Serializable {

	private static final long serialVersionUID = -1955844752879747204L;

	/**
	 * HTTP status code
	 */
	private int code;

	/**
	 * Error message
	 */
	private String message;

	/**
	 * Original exception class name
	 */
	private String original;

	protected RestException() {
		// for deserialization only
	}

	public RestException(Throwable exception, int defaultStatus) {

		Assert.notNull(exception, "Missing exception!");

		if (exception instanceof WebApplicationException) {
			code = ((WebApplicationException) exception).getResponse().getStatus();
		}
		else if (exception instanceof RestException) {
			code = ((RestException) exception).getResponse().getStatus();
		}
		else if (exception instanceof Failure) {
			code = ((Failure) exception).getErrorCode();
		}
		else { // default
			code = defaultStatus;
		}

		message = exception.getMessage();
		original = exception.getClass().getName();
	}

	public RestException(int status, String exceptionMessage) {

		code = status;
		message = exceptionMessage;
		original = null;
	}

	@JsonIgnore
	@Override
	public String toString() {

		try {
			return JsonUtils.getObjectMapper().writeValueAsString(this);
		}
		catch (IOException e) {
			// should not happen
			//log.error("Unexpected error", e);
			return "Error parsing the error!";
		}
	}

	@JsonProperty("code")
	public int getCode() {

		return code;
	}

	@JsonProperty("message")
	public String getMessage() {

		return message;
	}

	@JsonIgnore
	public String getOriginal() {

		return original;
	}

	/**
	 * Produces JSON wrapped exception response for RestEasy
	 *
	 * @return Rest easy exception as JSON formatted response
	 */
	@JsonIgnore
	public Response getResponse() {

		return Response.status(getCode())
			.entity(this)
			.type(MediaType.APPLICATION_JSON_TYPE)
			.build();
	}
}


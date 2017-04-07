package com.zandero.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zandero.server.rest.EchoHandler;
import com.zandero.server.service.SessionService;
import com.zandero.server.service.SessionServiceImpl;
import com.zandero.settings.Settings;

/**
 *
 */
public class BaseRestModule extends AbstractModule {

	private final Settings settings;

	public BaseRestModule(Settings restSettings) {

		settings = restSettings;
	}

	@Override
	protected void configure() {

		// Register RESTs
		bind(EchoHandler.class);

		// Register security filter
//		bind(AuthorizationFilter.class);

		// Register exception handling
	/*	bind(RestExceptionMapper.class); // mandatory for exception event filtering
		bind(RestRequestEventFilter.class); // optional in case request duration log should be added

		// Register async event handling and thread pool
		bind(RestResponseEventFilter.class); // mandatory for event filtering
		bind(RestEventThreadPool.class).to(RestEventThreadPoolImpl.class);*/

		// Register request scoped context
		//bind(RequestContext.class).to(BackendRequestContext.class);

		// Register all other services
		bind(SessionService.class).to(SessionServiceImpl.class);
	}

	@Provides
	Settings getSettings() {

		return settings;
	}
}

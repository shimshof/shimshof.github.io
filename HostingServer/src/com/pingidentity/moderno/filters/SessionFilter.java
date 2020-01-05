package com.pingidentity.moderno.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SessionFilter
 *
 * Created by Ping Identity on 3/23/17.
 * Copyright © 2017 Ping Identity. All rights reserved.
 */
public class SessionFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			logger.info("Sample hosting server received a new request");
			chain.doFilter(request, response);
		} catch (Throwable t) {
			logger.error(String.format("****Error occured: %s****", t));
			t.printStackTrace();
			throw t;
		}

	}

	@Override
	public void destroy() {
	}

}

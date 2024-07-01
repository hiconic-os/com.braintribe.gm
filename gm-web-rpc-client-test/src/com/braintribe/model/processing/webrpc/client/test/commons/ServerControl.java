// ============================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.webrpc.client.test.commons;

import java.net.URL;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import com.braintribe.processing.test.web.undertow.UndertowServer;
import com.braintribe.processing.test.web.undertow.UndertowServer.UndertowServerBuilder;

public class ServerControl {

	private static UndertowServer undertowServer;

	public static URL startServer(HttpServlet servlet, List<Filter> filters, String contextPath, String servletMapping) throws Exception {

		// @formatter:off
		UndertowServerBuilder builder = UndertowServer.create(contextPath);
		
		for (Filter filter : filters) {
			String filterName = filter.getClass().getSimpleName();
			builder.addFilter(filterName, filter);
			builder.addFilterUrlMapping(filterName, servletMapping, DispatcherType.REQUEST);
		}

		undertowServer = 
			builder
					.addServlet("rpc-server", servlet, servletMapping)
					.start();
		// @formatter:on

		URL rpcUrl = undertowServer.getServletUrl("rpc-server");

		return rpcUrl;

	}

	public static void shutdownServer() {
		undertowServer.stop();
	}

}

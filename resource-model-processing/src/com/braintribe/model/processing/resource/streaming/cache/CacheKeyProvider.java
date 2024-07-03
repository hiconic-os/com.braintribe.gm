// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
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
package com.braintribe.model.processing.resource.streaming.cache;

import java.util.function.Function;

import com.braintribe.logging.Logger;
import com.braintribe.model.resource.source.ConversionServiceSource;
import com.braintribe.model.resource.source.ResourceSource;


public class CacheKeyProvider implements Function<ResourceSource, String> {
	/**
	 * The logger.
	 */
	protected static Logger logger = Logger.getLogger(CacheKeyProvider.class);

	private Function<ConversionServiceSource, String> conversionServiceCacheKeyProvider = new ConversionServiceCacheKeyProvider();

	// **************************************************************************
	// Constructor
	// **************************************************************************

	/**
	 * Default constructor
	 */
	public CacheKeyProvider() {
	}

	// **************************************************************************
	// Getter/Setter
	// **************************************************************************

	public void setConversionServiceCacheKeyProvider(
			Function<ConversionServiceSource, String> conversionServiceCacheKeyProvider) {
		this.conversionServiceCacheKeyProvider = conversionServiceCacheKeyProvider;
	}

	// **************************************************************************
	// Interface implementations
	// **************************************************************************

	/**
	 * @see Function#apply(java.lang.Object)
	 */
	@Override
	public String apply(ResourceSource source) throws RuntimeException {
		if (source instanceof ConversionServiceSource && conversionServiceCacheKeyProvider != null) {
			ConversionServiceSource conversionSource = (ConversionServiceSource) source;
			return conversionServiceCacheKeyProvider.apply(conversionSource);
		} else {
			return source.getId();
		}
	}

	public static String getBuildVersion() {
		return "$Build_Version$ $Id: CacheKeyProvider.java 102880 2018-01-18 11:36:53Z roman.kurmanowytsch $";
	}
}

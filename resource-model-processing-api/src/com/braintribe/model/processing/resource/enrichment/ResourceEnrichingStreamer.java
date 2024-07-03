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
package com.braintribe.model.processing.resource.enrichment;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resource.Resource;

/**
 * <p>
 * Enriches {@link Resource} meta data while streaming.
 * 
 */
public interface ResourceEnrichingStreamer {

	ResourceEnrichingStreamerBuilder stream();

	interface ResourceEnrichingStreamerBuilder {

		ResourceEnrichingStreamerBuilder from(Supplier<InputStream> inputSupplier);

		ResourceEnrichingStreamerBuilder to(Supplier<OutputStream> outputSupplier);

		ResourceEnrichingStreamerBuilder context(ServiceRequestContext context);

		ResourceEnrichingStreamerBuilder onlyIfEnriched();

		boolean enriching(Resource resource);

		/**
		 * EXPERIMENTAL!!!
		 * 
		 * This might change without warning, use at your own risk!!!
		 */
		EnrichingResult enriching2(Resource resource);

	}

	/**
	 * EXPERIMENTAL!!!
	 * 
	 * This might change without warning, use at your own risk!!!
	 */
	interface EnrichingResult {
		boolean enriched();

		InputStream inputStream();
	}

}

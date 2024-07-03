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
package com.braintribe.cartridge.common.processing.streaming;
// ============================================================================
// BRAINTRIBE TECHNOLOGY GMBH - www.braintribe.com
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2018 - All Rights Reserved
// It is strictly forbidden to copy, modify, distribute or use this code without written permission
// To this file the Braintribe License Agreement applies.
// ============================================================================



import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.braintribe.cfg.Configurable;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.accessrequest.api.AccessRequestContext;
import com.braintribe.model.processing.resource.enrichment.ResourceEnricher;
import com.braintribe.model.processing.resource.enrichment.ResourceSpecificationDetector;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.specification.ResourceSpecification;
import com.braintribe.model.resourceapi.enrichment.EnrichResource;
import com.braintribe.model.resourceapi.enrichment.EnrichResourceResponse;

/**
 * <p>
 * A standard {@link ResourceEnricher} for enriching {@link Resource}(s) after they are persisted.
 * 
 * <p>
 * The following property is set by this {@link ResourceEnricher}:
 * 
 * <ul>
 * <li>{@link Resource#setSpecification(com.braintribe.model.resource.specification.ResourceSpecification)}</li>
 * </ul>
 * 
 */
public class StandardResourcePostPersistenceEnricher implements ResourceEnricher, StandardResourceProcessor, ServiceProcessor<EnrichResource, EnrichResourceResponse>  {

	private ResourceSpecificationDetector<?> specificationDetector;

	@Configurable
	public void setSpecificationDetector(ResourceSpecificationDetector<?> specificationDetector) {
		this.specificationDetector = specificationDetector;
	}

	@Override
	public EnrichResourceResponse enrich(AccessRequestContext<EnrichResource> context) {
		return process(context, context.getOriginalRequest());
	}
	
	@Override
	public EnrichResourceResponse process(ServiceRequestContext context, EnrichResource request) {

		Resource resource = request.getResource();

		Objects.requireNonNull(resource, "request's resource");

		EnrichResourceResponse response = EnrichResourceResponse.T.create();

		String mimeType = resource.getMimeType();

		if (resource.getSpecification() == null && specificationDetector != null && mimeType != null) {

			@SuppressWarnings("cast")
			GmSession session = (context instanceof AccessRequestContext<?>) ? ((AccessRequestContext<?>) context).getSession() : null;

			ResourceSpecification specification = null;

			try (InputStream in = resource.openStream()) {

				specification = specificationDetector.getSpecification(in, mimeType, session);

			} catch (IOException e) {
				throw unchecked("Failed to detect the specification", e);
			}

			if (specification != null) {
				resource.setSpecification(specification);
				response.setResource(resource);
			}

		}

		return response;

	}

}

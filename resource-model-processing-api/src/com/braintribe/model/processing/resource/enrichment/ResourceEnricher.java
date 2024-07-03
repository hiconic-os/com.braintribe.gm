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

import com.braintribe.model.processing.accessrequest.api.AccessRequestContext;
import com.braintribe.model.resourceapi.enrichment.EnrichResource;
import com.braintribe.model.resourceapi.enrichment.EnrichResourceResponse;

/**
 * <p>
 * An {@link AccessRequestContext}-bound processor of {@link EnrichResource} instances.
 * 
 * @author dirk.scheffler
 */
public interface ResourceEnricher extends ResourceEnricherDefaults {

	EnrichResourceResponse enrich(AccessRequestContext<EnrichResource> context);
	
}

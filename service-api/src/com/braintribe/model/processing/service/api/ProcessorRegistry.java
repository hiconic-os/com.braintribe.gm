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
package com.braintribe.model.processing.service.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.service.impl.ServiceProcessors;
import com.braintribe.model.service.api.ServiceRequest;

public interface ProcessorRegistry {

	<R extends ServiceRequest> void register(EntityType<R> requestType, ServiceProcessor<? super R, ?> serviceProcessor);

	default <R extends ServiceRequest> void registerMapped(EntityType<R> requestType, MappingServiceProcessor<? super R, ?> serviceProcessor) {
		register(requestType, ServiceProcessors.dispatcher(serviceProcessor));
	}

}

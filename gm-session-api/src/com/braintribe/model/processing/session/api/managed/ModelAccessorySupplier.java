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
package com.braintribe.model.processing.session.api.managed;

public interface ModelAccessorySupplier {

	/**
	 * Returns a {@link ModelAccessory} for given model name.
	 * <p>
	 * IMPORTANT: It may return an instance even if no such model exists, in which case only the subsequent usage (e.g. calling
	 * {@link ModelAccessory#getCmdResolver()}) would throw an exception.
	 */
	default ModelAccessory getForModel(@SuppressWarnings("unused") String modelName) {
		throw new UnsupportedOperationException("getForModel(String) is not supported by " + getClass().getName());
	}

}

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
package com.braintribe.model.processing.service.api;

import com.braintribe.model.processing.service.impl.BasicOutputConfig;

/**
 * <p>
 * Wrapper holding output configuration specifics.
 * 
 * <p>
 * An empty configuration can be used to return default settings in case no related aspect configuration was found,
 * see {@link ServiceRequestContext#getAspect(Class, Object)}.
 * 
 * @author christina.wilpernig
 */
public interface OutputConfig {

	boolean verbose();
	
	default boolean dynamic() {
		return true;
	}

	OutputConfig empty = new BasicOutputConfig(false);

}

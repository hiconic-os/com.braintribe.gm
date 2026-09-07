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
package com.braintribe.gm.config.yaml.api;

import java.util.Set;

/**
 * Result of a build-time configuration read.
 * <p>
 * Variables which could be resolved have already been evaluated. Variables which were genuinely unavailable remain as value descriptors in the
 * configuration and are listed in {@link #unresolvedVariables()}. Syntax, type conversion and resolver failures are not made lenient and still make
 * the read fail.
 */
public record PartiallyResolvedConfiguration<C>(C configuration, Set<String> unresolvedVariables) {

	public PartiallyResolvedConfiguration {
		unresolvedVariables = Set.copyOf(unresolvedVariables);
	}
}

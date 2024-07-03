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
package com.braintribe.gm.config.yaml.wire.contract;

import java.io.File;

import com.braintribe.wire.api.module.WireModule;
import com.braintribe.wire.api.space.WireSpace;

/**
 * This contract is unbound in this {@link WireModule} and meant to be bound by another {@link WireModule} to supply this module with a configuration dir from
 * which yaml serialized modeled data will be read. 
 */
public interface YamlConfigurationsLocationContract extends WireSpace {
	File configDir();
	default String resolveVariable(String name) { return null; } 
}

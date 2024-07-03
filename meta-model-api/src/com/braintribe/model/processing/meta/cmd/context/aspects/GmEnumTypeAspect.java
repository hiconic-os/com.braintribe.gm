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
package com.braintribe.model.processing.meta.cmd.context.aspects;

import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.processing.meta.cmd.context.scope.ScopedAspect.StaticScopedAspect;

/**
 * A {@link GmEnumTypeAspect} aspect. This aspect is added to the context automatically when an enum (or enum constant)
 * is specified.
 */
public class GmEnumTypeAspect extends StaticScopedAspect<GmEnumType> {
	// blank
}

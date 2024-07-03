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
package com.braintribe.codec.marshaller.api;

import com.braintribe.common.attribute.TypeSafeAttribute;

/**
 * This interface marks a {@link TypeSafeAttribute} which is usually meant to be supported by certain
 * {@link Marshaller}s. It has rather semantic than functional meaning and might for example be used to search for
 * marshalling related attributes with your IDE.
 */
public interface MarshallerOption<T> extends TypeSafeAttribute<T> {
	// Marker interface to mark attributes that are meant to be used as marshaller option
}

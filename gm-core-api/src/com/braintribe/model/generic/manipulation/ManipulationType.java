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
package com.braintribe.model.generic.manipulation;

/**
 * Describes the type of {@link Manipulation}. Each such manipulation (non-abstract) must return a correct value by implementing the
 * {@link Manipulation#manipulationType()} method. This value is used to speed up the branching in case the different manipulations should
 * be handled in different ways (switch statement is much faster than a long chain of {@code instanceof} operators).
 */
public enum ManipulationType {

	DELETE,
	INSTANTIATION,
	ACQUIRE,
	MANIFESTATION,

	// PROPERTY
	ABSENTING,
	CHANGE_VALUE,

	// COLLECTION
	ADD,
	REMOVE,
	CLEAR_COLLECTION,

	// meta
	VOID,
	COMPOUND;
}

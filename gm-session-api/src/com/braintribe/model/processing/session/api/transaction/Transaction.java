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
package com.braintribe.model.processing.session.api.transaction;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.session.GmSession;

public interface Transaction extends TransactionFrame {

	TransactionFrame getCurrentTransactionFrame();

	Set<LocalEntityProperty> getManipulatedProperties();

	boolean hasManipulations();

	/**
	 * Returns <tt>true</tt> iff given entity was created within this transaction. Note that this returns true even if
	 * the entity was later {@link GmSession#deleteEntity deleted} or {@link #undo undone}, which is handled by
	 * {@link #willPersist(GenericEntity)}.
	 */
	boolean created(GenericEntity entity);

	/**
	 * Similar to {@link #created(GenericEntity)}, but this returns <tt>false</tt> for {@link GmSession#deleteEntity
	 * deleted} or {@link #undo undone} entities.
	 */
	boolean willPersist(GenericEntity entity);

}

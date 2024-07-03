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
package com.braintribe.model.processing.am;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.session.api.notifying.NotifyingGmSession;
import com.braintribe.model.processing.session.impl.session.collection.EnhancedList;
import com.braintribe.model.processing.session.impl.session.collection.EnhancedMap;
import com.braintribe.model.processing.session.impl.session.collection.EnhancedSet;

/**
 * 
 */
public interface AssemblyMonitoringBuilder {

	// ###################################
	// ## . . . . . . Setters . . . . . ##
	// ###################################

	void setIsAbsenceResolvable(boolean isAbsenceResolvable);

	// ###################################
	// ## . . . . . . Getters . . . . . ##
	// ###################################

	boolean isAbsenceResolvable();

	NotifyingGmSession getSession();

	// ###################################
	// ## . . . . . . Builds . . . . . .##
	// ###################################

	AssemblyMonitoring build(NotifyingGmSession session, GenericEntity root);

	AssemblyMonitoring build(NotifyingGmSession session, GenericEntity root, Property property);

	AssemblyMonitoring build(NotifyingGmSession session, EnhancedList<?> list);

	AssemblyMonitoring build(NotifyingGmSession session, EnhancedSet<?> set);

	AssemblyMonitoring build(NotifyingGmSession session, EnhancedMap<?, ?> map);

}

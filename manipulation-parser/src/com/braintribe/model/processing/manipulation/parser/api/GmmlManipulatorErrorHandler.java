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
package com.braintribe.model.processing.manipulation.parser.api;

import java.util.logging.StreamHandler;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.manipulation.parser.impl.listener.GmmlManipulatorParserListener;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.LenientErrorHandler;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.TrackingErrorHandler;

/**
 * Handler for various non-syntax related errors that occur while parsing/applying GMML with {@link GmmlManipulatorParserListener}.
 * 
 * @see StreamHandler
 * @see LenientErrorHandler
 * @see TrackingErrorHandler
 * 
 * @author peter.gazdik
 */
public interface GmmlManipulatorErrorHandler {

	default void onStart() {
		// Optional
	}

	default void onEnd() {
		// Optional
	}

	void typeNotFound(String typeSignature);

	void propertyNotFound(GenericEntity entity, String propertyName);

	void enumConstantNotFound(EnumType enumType, String enumConstantName);

	void entityNotFound(String globalId);

	/** @see GmmlManipulatorParserConfiguration#problematicEntitiesRegistry() */
	void problematicEntityReferenced(String globalId);

	void variableNotEntityType(String variableName);

	void propertyNotCollection(GenericEntity entity, Property property);

	void propertyValueNotCollectionCannotClear(GenericEntity entity, Property property, Object value);

	void propertySettingFailed(GenericEntity entity, Property property, Object value, RuntimeException e);

	void wrongValueTypeToAddToCollection(Object value, CollectionType type);

	void wrongTypeForListAdd(Object index, boolean indexOk, Object value, boolean valueOk, GenericModelType valueType);

	void wrongTypeForMapPut(Object key, boolean keyOk, Object value, boolean valueOk, MapType type);

	void cannotResolvePropertyOfNonEntity(Object nonEntity, String propertyName);

	void typeNotGenericModelType(Object type);

	// GlobalId

	void globalIdSettingFailed(GenericEntity entity, Object globalId, RuntimeException e);

	/**
	 * Unreachable if {@link #globalIdSettingFailed(GenericEntity, Object, RuntimeException)} throws an exception (i.e. not handled leniently).
	 */
	void globalIdAdjusted(GenericEntity entity, Object globalId, String newGlobalId, RuntimeException e);

}

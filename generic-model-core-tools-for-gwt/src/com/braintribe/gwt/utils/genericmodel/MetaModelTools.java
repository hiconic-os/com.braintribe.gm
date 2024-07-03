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
package com.braintribe.gwt.utils.genericmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.utils.lcd.CommonTools;

/**
 * Provides {@link GmMetaModel} related utility and convenience methods.
 * 
 * @deprecated All methods provide functionality available via {@link ModelOracle}, so please ajust the code to use that one.
 */
@Deprecated
public class MetaModelTools {

	/**
	 * Finds all (i.e. also inherited) properties for given {@link GmEntityType}. The method guarantees that every property is contained
	 * within the result just once (i.e. there are no two properties with same name).
	 * <p>
	 * If a property is overridden in the sub-entity, the method tries to take the {@link GmProperty} object from the sub-entity, but (in
	 * case of multiple inheritance) this behavior is not guaranteed.
	 * 
	 * @deprecated user {@link ModelOracle}
	 */
	@Deprecated
	public static Collection<GmProperty> findAllProperties(final GmEntityType gmEntityType) {
		final Map<String, GmProperty> result = new HashMap<String, GmProperty>();

		addAll(result, gmEntityType);

		return result.values();
	}

	private static void addAll(final Map<String, GmProperty> result, final GmEntityType gmEntityType) {
		final List<GmEntityType> superTypes = gmEntityType.getSuperTypes();

		if (superTypes != null) {
			for (final GmEntityType superType: superTypes) {
				addAll(result, superType);
			}
		}

		final List<GmProperty> properties = gmEntityType.getProperties();

		if (properties != null) {
			for (final GmProperty gmProperty: properties) {
				result.put(gmProperty.getName(), gmProperty);
			}
		}
	}

	/**
	 * Gets the {@link GmEntityType} with the specified <code>entityTypeSignature</code> or <code>null</code>, if it doesn't exist in the
	 * passed model.
	 * 
	 * @deprecated use {@link ModelOracle} framework for stuff like this. Or is there any reason not to?
	 */
	@Deprecated
	public static GmEntityType getEntityType(final GmMetaModel metaModel, final String entityTypeSignature) {
		for (final GmType gmType: metaModel.getTypes()) {
			if (entityTypeSignature.equals(gmType.getTypeSignature())) {
				return (GmEntityType) gmType;
			}
		}
		return null;
	}

	/**
	 * Gets the {@link GmEntityType} with the specified <code>entityTypeSignature</code>.
	 *
	 * @throws IllegalArgumentException
	 *             if the searched type doesn't exist in the passed model.
	 * 
	 * @deprecated use {@link ModelOracle} framework for stuff like this. Or is there any reason not to?
	 */
	@Deprecated
	public static GmEntityType getExistingEntityType(final GmMetaModel metaModel, final String entityTypeSignature) {
		final GmEntityType result = getEntityType(metaModel, entityTypeSignature);
		if (result == null) {
			throw new IllegalArgumentException("The searched entity type is not part of the passed metamodel! " +
					CommonTools.getParametersString("entityTypeSignature", entityTypeSignature));
		}
		return result;
	}
}

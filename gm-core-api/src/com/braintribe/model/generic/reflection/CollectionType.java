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
package com.braintribe.model.generic.reflection;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.base.CollectionBase;

import jsinterop.annotations.JsType;

@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
public interface CollectionType extends EnhancableCustomType {

	@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
	enum CollectionKind {
		map,
		set,
		list
	}

	/**
	 * {@inheritDoc}
	 * 
	 * ATTENTION: The implementation actually does the check: {@code this == typeReflection.getType(value)}, which means
	 * two things:
	 * <ul>
	 * <li>If given collection doesn't implement {@link CollectionBase}, this method returns <tt>true</tt> iff
	 * <tt>this</tt> collection's parameterization consists of only Objects.</li>
	 * <li>If given collection does implement {@link CollectionBase}, this method returns <tt>true</tt> iff the types
	 * match exactly, so e.g. a set of {@link GenericEntity} is not an instance of set of Objects.</li>
	 * </ul>
	 * 
	 * @see CollectionBase
	 */
	@Override
	boolean isInstance(Object value);

	/**
	 * @returns <tt>true</tt> iff given collections may ONLY contain simple or enum types. In other words, in case of a
	 *          map this is true if both key and value are either simple or enum.
	 * 
	 * @see GenericModelType#areCustomInstancesReachable()
	 * @see GenericModelType#areEntitiesReachable()
	 */
	boolean hasSimpleOrEnumContent();

	CollectionKind getCollectionKind();

	/**
	 * Returns:
	 * <ul>
	 * <li>Element type - if <tt>List</tt> or <tt>Set</tt></li>
	 * <li>Value type - if <tt>Map</tt></li>
	 * </ul>
	 * 
	 * @see MapType#getValueType()
	 * @see MapType#getKeyType()
	 */
	GenericModelType getCollectionElementType();

	/**
	 * Static methods for generating type signature
	 */
	class TypeSignature {
		public static String forList(String elementSignature) {
			return "list<" + elementSignature + '>';
		}

		public static String forSet(String elementSignature) {
			return "set<" + elementSignature + '>';
		}

		public static String forMap(String keySignature, String valueSignature) {
			return "map<" + keySignature + ',' + valueSignature + '>';
		}
	}
}

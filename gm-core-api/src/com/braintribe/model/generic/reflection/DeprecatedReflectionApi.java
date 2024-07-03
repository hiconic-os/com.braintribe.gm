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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.value.EntityReference;

public interface DeprecatedReflectionApi {

	/** @deprecated use {@link SimpleTypes#TYPES_SIMPLE} */
	@Deprecated
	List<SimpleType> stSimpleTypes = SimpleTypes.TYPES_SIMPLE;

	/** @deprecated call {@link GenericEntity#entityType()} directly */
	@Deprecated
	<T extends GenericEntity, T1 extends GenericEntity> EntityType<T1> getEntityType(T entity) throws GenericModelException;

	/**
	 * @deprecated use {@link GenericModelTypeReflection#getType(Object)} followed by
	 *             {@link GenericModelType#getTypeSignature()}
	 */
	@Deprecated
	String getTypeSignature(Object value) throws GenericModelException;

	/** @deprecated call {@link GenericEntity#reference()} directly */
	@Deprecated
	<T extends GenericEntity> EntityReference getEntityReference(T entity) throws GenericModelException;

	/** @deprecated use {@link GenericModelTypeReflection#getType(String)} */
	@Deprecated
	GenericModelType getRegisteredType(String typeSignature) throws GenericModelException;

	/** @deprecated use {@link SimpleTypes#TYPES_SIMPLE} */
	@Deprecated
	List<SimpleType> getSimpleTypes();

	/** @deprecated use {@link SimpleTypes#TYPES_SIMPLE} of {@link GenericModelTypeReflection#getType(Class)} */
	@Deprecated
	Map<Class<?>, SimpleType> getSimpleTypeMap();

	/** @deprecated use {@link GenericModelTypeReflection#getType(String)} */
	@Deprecated
	Map<String, SimpleType> getSimpleTypeNameMap();

	/** @deprecated use {@link GenericModelTypeReflection#getType(Object)} */
	@Deprecated
	Set<Class<?>> getSimpleTypeClasses();

	/** @deprecated not sure why anyone would need this... */
	@Deprecated
	Set<String> getSimpleTypeNames();

	/** @deprecated use {@link GenericModelTypeReflection#getType(Type)} */
	@Deprecated
	GenericModelType getGenericModelType(Type type) throws GenericModelException;

	/** @deprecated use {@link GenericModelTypeReflection#getType(Class)} */
	@Deprecated
	GenericModelType getGenericModelType(Class<?> declarationIface) throws GenericModelException;

	/** @deprecated use {@link GenericModelTypeReflection#getType(Object)} */
	@Deprecated
	GenericModelType getObjectType(Object value);

	/**
	 * @deprecated use either {@link GenericModelTypeReflection#getType(String)} or
	 *             {@link GenericModelTypeReflection#findType(String)}.
	 */
	@Deprecated
	EnumType getEnumType(String typeName, boolean require);

	/**
	 * @deprecated use {@link GenericModelTypeReflection#getType(String)} or
	 *             {@link GenericModelTypeReflection#findType(String)}
	 */
	@Deprecated
	<T extends GenericEntity> EntityType<T> getEntityType(String typeSignature, boolean require) throws GenericModelException;
}

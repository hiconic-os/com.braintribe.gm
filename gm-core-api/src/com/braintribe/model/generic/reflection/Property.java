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
import com.braintribe.model.generic.base.EntityBase;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.generic.value.ValueDescriptor;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import jsinterop.annotations.custom.TsUnignoreMethod;

/**
 * 
 */
@JsType(namespace=GmCoreApiInteropNamespaces.reflection)
@SuppressWarnings("unusable-by-js")
public interface Property extends Attribute {

	GenericModelType getType();

	/**
	 * Returns true based on the following rules:
	 * <ol>
	 * <li>The value is null</li>
	 * <li>The type of the property is a collection and the value is an empty collection (special case of {@link GenericModelType#isEmpty(Object)}
	 * )</li>
	 * <li>The property is not nullable and the value is equal to the default value of the property (see {@link #getDefaultRawValue()})</li>
	 * </ol>
	 */
	@JsIgnore // See PropertyJs in gwt-gm-core
	@TsUnignoreMethod
	boolean isEmptyValue(Object value);

	/** @return <tt>true</tt> iff this property is {@link GenericEntity#id} */
	boolean isIdentifier();

	/** returns <tt>true</tt> iff this is the {@link GenericEntity#partition} property */
	boolean isPartition();

	/** returns <tt>true</tt> iff this is the {@link GenericEntity#globalId} property */
	boolean isGlobalId();

	/**
	 * @return <tt>true</tt> iff this property is either an {@link #isIdentifier() identifier} or it's the {@link #isPartition() partition} property
	 *         (i.e. is used to uniquely identify an entity)
	 */
	boolean isIdentifying();

	/**
	 * Says whether given property is confidential, i.e. whether it was declared with the confidential annotation and/or meta-data. One should be
	 * extra careful when handling a confidential property, see for example {@link EntityBase#toString()}.
	 */
	boolean isConfidential();
	
	/**
	 * @return initializer (i.e. configured initial value) for a given property, which may either be the value directly (when simple), or a
	 *         {@link ValueDescriptor} instance. Enums are represented as VDs, namely {@link EnumReference}s.
	 */
	Object getInitializer();

	/** with AOP, wrap to VdHolder, set directly without type-check */
	<T extends ValueDescriptor> T getVd(GenericEntity entity);
	/** @see #getVd */
	void setVd(GenericEntity entity, ValueDescriptor value);

	/** no AOP, wraps to VdHolder and sets directly, without AOP or type-check */
	<T extends ValueDescriptor> T getVdDirect(GenericEntity entity);
	/** @see #getVdDirect */
	Object setVdDirect(GenericEntity entity, ValueDescriptor value);

	/** Returns the AbsenceInformation for given entity, or null, of the property is not absent */
	AbsenceInformation getAbsenceInformation(GenericEntity entity);
	/** @see #getAbsenceInformation */
	void setAbsenceInformation(GenericEntity entity, AbsenceInformation ai);

	/**
	 * @return true iff this property is absent, i.e. if the property field references a VdHolder which is marked as an absence information.
	 */
	boolean isAbsent(GenericEntity entity);

	/**
	 * @return the default value of this property, i.e. the value of the newly created instance. This means, if this property has an initializer, the
	 *         initializer value will be returned. If not, and the property is nullable, <tt>null</tt> is returned, otherwise the
	 *         {@link SimpleType#getDefaultValue() defaultValue} of the corresponding {@link SimpleType} is returned.
	 */
	Object getDefaultValue();

	/**
	 * @return the default value based on the property type, or <tt>null</tt> if the property is nullable. This means that for a property of type
	 *         Integer the result would be <tt>null</tt>, but for int it would be 0.
	 */
	Object getDefaultRawValue();

	/**
	 * {@inheritDoc}
	 * <p>
	 * Setting a {@link ValueDescriptor} this way is not possible, use one of {@link #setVd(GenericEntity, ValueDescriptor)},
	 * {@link #setVdDirect(GenericEntity, ValueDescriptor)} or {@link #setDirectUnsafe(GenericEntity, Object)}.
	 */
	@Override
	Object setDirect(GenericEntity entity, Object value);

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method can also be used to set a {@link ValueDescriptor} on the property, if you supply the VdHolder instance.
	 */
	@Override
	void setDirectUnsafe(GenericEntity entity, Object value);
}

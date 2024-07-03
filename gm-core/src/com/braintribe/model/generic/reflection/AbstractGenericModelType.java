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

import java.util.ConcurrentModificationException;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.generic.pr.criteria.RootCriterion;
import com.braintribe.model.generic.pr.criteria.matching.Matcher;
import com.braintribe.model.generic.reflection.type.BaseTypeImpl;
import com.braintribe.model.generic.reflection.type.collection.AbstractCollectionType;
import com.braintribe.model.generic.reflection.type.custom.AbstractEntityType;
import com.braintribe.model.generic.reflection.type.custom.EnumTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.AbstractSimpleType;

public abstract class AbstractGenericModelType implements GenericModelType {

	protected Class<?> javaType;

	protected AbstractGenericModelType(Class<?> javaType) {
		this.javaType = javaType;
	}

	@Override
	public Class<?> getJavaType() {
		return javaType;
	}

	@Override
	public abstract TypeCode getTypeCode();

	@Override
	public abstract String getTypeName();

	@Override
	public abstract String getSelectiveInformation(Object instance);

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public final <T> T clone(CloningContext cloningContext, Object instance, StrategyOnCriterionMatch strategy) throws GenericModelException {
		if (strategy == null)
			strategy = cloningContext.getStrategyOnCriterionMatch();

		RootCriterion rootCriterion = RootCriterion.T.createPlain();
		rootCriterion.setTypeSignature(getActualType(instance).getTypeSignature());
		cloningContext.pushTraversingCriterion(rootCriterion, instance);
		try {
			if (!cloningContext.isTraversionContextMatching()) {
				return (T) cloneImpl(cloningContext, instance, strategy);
			} else {
				return null;
			}
		} catch (ConcurrentModificationException cme) {
			Exceptions.contextualize(cme, "Error while trying to clone instance: " + instance);
			throw cme;
		} finally {
			cloningContext.popTraversingCriterion();
		}
	}

	/**
	 * This method returns in the most specified type statement seen from assignable perspective.
	 */
	@Override
	public GenericModelType getActualType(Object value) {
		return this;
	}

	public abstract Object cloneImpl(CloningContext cloningContext, Object instance, StrategyOnCriterionMatch strategy) throws GenericModelException;

	@Override
	public final void traverse(TraversingContext traversingContext, Object instance) throws GenericModelException {
		RootCriterion rootCriterion = RootCriterion.T.createPlain();
		rootCriterion.setTypeSignature(getActualType(instance).getTypeSignature());
		traversingContext.pushTraversingCriterion(rootCriterion, instance);
		try {
			if (!traversingContext.isTraversionContextMatching())
				traverseImpl(traversingContext, instance);

		} finally {
			traversingContext.popTraversingCriterion();
		}
	}

	public abstract void traverseImpl(TraversingContext traversingContext, Object instance) throws GenericModelException;

	@Override
	public TraversingContext traverse(Object instance, Matcher matcher, TraversingVisitor traversingVisitor) throws GenericModelException {
		StandardTraversingContext traversingContext = new StandardTraversingContext();
		traversingContext.setMatcher(matcher);
		traversingContext.setTraversingVisitor(traversingVisitor);
		traverse(traversingContext, instance);
		return traversingContext;
	}

	@Override
	public final <T> T clone(Object instance, Matcher matcher, StrategyOnCriterionMatch strategy) throws GenericModelException {
		StandardCloningContext cloningContext = new StandardCloningContext();
		cloningContext.setMatcher(matcher);
		return clone(cloningContext, instance, strategy);
	}

	private static final GenericModelType[] emptyParameterization = new GenericModelType[] {};

	@Override
	public GenericModelType[] getParameterization() {
		return emptyParameterization;
	}

	/**
	 * Returns a "snapshot" (i.e. current state) of given value. Typical use-case is the value to be set for a manipulation
	 * (e.g. ChangeValueManipulation). This value may in most cases be the original object, but in case of collections
	 * (including maps), we have to create a copy (because if we used the original collection and changed it later, we would
	 * also change the manipulation).
	 */
	@Override
	public Object getValueSnapshot(Object value) {
		return value;
	}

	@Override
	public String getTypeSignature() {
		return getTypeName();
	}

	/** This is overridden in the {@link BaseTypeImpl} subclass. */
	@Override
	public boolean isBase() {
		return false;
	}

	/** This is overridden in the {@link AbstractCollectionType} subclass. */
	@Override
	public boolean isCollection() {
		return false;
	}

	/** This is overridden in the {@link AbstractEntityType} subclass. */
	@Override
	public boolean isEntity() {
		return false;
	}

	/** This is overridden in the {@link AbstractEntityType} subclass. */
	@Override
	public boolean isVd() {
		return false;
	}

	/** This is overridden in the {@link EnumTypeImpl} subclass. */
	@Override
	public boolean isEnum() {
		return false;
	}

	/** This is overridden in the {@link AbstractSimpleType} subclass. */
	@Override
	public boolean isSimple() {
		return false;
	}

	/** This is overridden in the specific numeric type implementations. */
	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public boolean isScalar() {
		return false;
	}

	@Override
	public <T extends GenericModelType> T cast() {
		return (T) this;
	}

	@Override
	public String toString() {
		return "type " + getTypeSignature();
	}

	@Override
	public int compareTo(GenericModelType o) {
		if (o == null) {
			return 1;
		}

		if (this == o)
			return 0;

		String myTypeSignature = this.getTypeSignature();
		String otherTypeSignature = o.getTypeSignature();

		return myTypeSignature.compareTo(otherTypeSignature);
	}

}

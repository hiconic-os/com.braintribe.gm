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

import java.util.Collection;
import java.util.Stack;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.pr.criteria.BasicCriterion;
import com.braintribe.model.generic.pr.criteria.CriterionType;

import jsinterop.annotations.JsType;

@JsType(namespace = GmCoreApiInteropNamespaces.reflection)
@SuppressWarnings("unusable-by-js")
public interface TraversingContext {

	Stack<BasicCriterion> getTraversingStack();

	Stack<Object> getObjectStack();

	void pushTraversingCriterion(BasicCriterion criterion, Object object);

	BasicCriterion popTraversingCriterion();

	boolean isTraversionContextMatching();

	/**
	 * Registers the <code>entity</code> as {@link #getVisitedObjects() visited}.
	 * 
	 * @param entity
	 *            the visited entity
	 * @param associate
	 *            an optional associated object. Note that this is not really needed on {@link TraversingContext} level,
	 *            but it can be useful in some contexts (e.g. the <code>associate</code> could be the cloned entity).
	 */
	void registerAsVisited(GenericEntity entity, Object associate);

	<T> T getAssociated(GenericEntity entity);

	/**
	 * Returns all the entities that have been {@link #registerAsVisited(GenericEntity, Object) visited}. Note that this
	 * has NOTHING to do with {@link TraversingVisitor}s. The main purpose of this method is make sure that entities are
	 * only traversed once. Therefore it returns the entities that have been traversed (on {@link CriterionType#ENTITY}
	 * level).
	 */
	Collection<GenericEntity> getVisitedObjects();

	<T> Collection<T> getAssociatedObjects();

	/**
	 * Returns whether the <code>entity</code> has been {@link #getVisitedObjects() visited}.
	 */
	boolean isVisited(GenericEntity entity);

	boolean isPropertyValueUsedForMatching(EntityType<?> type, GenericEntity entity, Property property);

	boolean isAbsenceResolvable(Property property, GenericEntity entity, AbsenceInformation absenceInformation);

	/**
	 * Returns the {@link CriterionType} for {@link BasicCriterion} that is at the top of the traversing stack. If the
	 * stack is empty, the behavior of this method is not specified.
	 */
	CriterionType getCurrentCriterionType();
}

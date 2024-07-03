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
package com.braintribe.model.generic.path;

import java.util.Collection;
import java.util.Stack;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.pr.criteria.BasicCriterion;
import com.braintribe.model.generic.pr.criteria.CriterionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TraversingContext;

public class MpTraversingContext implements TraversingContext {
	private Stack<BasicCriterion> criterionStack = new Stack<BasicCriterion>();
	private Stack<Object> objectStack = new Stack<Object>();

	@Override
	public Stack<BasicCriterion> getTraversingStack() {
		return criterionStack;
	}

	@Override
	public Stack<Object> getObjectStack() {
		return objectStack;
	}

	@Override
	public void pushTraversingCriterion(BasicCriterion criterion, Object object) {
		criterionStack.push(criterion);
		objectStack.push(object);
	}

	@Override
	public BasicCriterion popTraversingCriterion() {
		objectStack.pop();
		return criterionStack.pop();
	}

	@Override
	public boolean isTraversionContextMatching() {
		return false;
	}

	@Override
	public void registerAsVisited(GenericEntity entity, Object associate) {
		throw new UnsupportedOperationException("not implemented by intention");
		
	}

	@Override
	public <T> T getAssociated(GenericEntity entity) {
		throw new UnsupportedOperationException("not implemented by intention");
	}

	@Override
	public Collection<GenericEntity> getVisitedObjects() {
		throw new UnsupportedOperationException("not implemented by intention");
	}

	@Override
	public <T> Collection<T> getAssociatedObjects() {
		throw new UnsupportedOperationException("not implemented by intention");	
	}

	@Override
	public boolean isVisited(GenericEntity entity) {
		throw new UnsupportedOperationException("not implemented by intention");
	}

	@Override
	public boolean isPropertyValueUsedForMatching(EntityType<?> type, GenericEntity entity, Property property) {
		return true;
	}

	@Override
	public boolean isAbsenceResolvable(Property property, GenericEntity entity, AbsenceInformation absenceInformation) {
		return false;
	}

	@Override
	public CriterionType getCurrentCriterionType() {
		return criterionStack.isEmpty()? criterionStack.peek().criterionType(): null;
	}
}

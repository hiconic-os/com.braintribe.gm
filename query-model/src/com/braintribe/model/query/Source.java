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
package com.braintribe.model.query;

import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;

/**
 * An {@link Operand} that encapsulates the sources for which the {@link Query} request will be applied.
 */
public interface Source extends Operand {

	EntityType<Source> T = EntityTypes.T(Source.class);

	String getName();
	void setName(String name);

	void setJoins(Set<Join> joins);
	Set<Join> getJoins();
	
	default Join join(String propertyName) {
		Join join = Join.T.create();
		this.getJoins().add(join);
		join.setProperty(propertyName);
		join.setSource(this);
		return join;
	}
	
	default Join join(String propertyName, JoinType joinType) {
		Join join = Join.T.create();
		this.getJoins().add(join);
		join.setProperty(propertyName);
		join.setSource(this);
		join.setJoinType(joinType);
		return join;
	}
	
	default Join leftJoin(String propertyName) {
		return join(propertyName, JoinType.left);
	}
	
	default Join rightJoin(String propertyName) {
		return join(propertyName, JoinType.right);
	}
	
	default Join innerJoin(String propertyName) {
		return join(propertyName, JoinType.inner);
	}
	
	default Join fullJoin(String propertyName) {
		return join(propertyName, JoinType.full);
	}
	
	default PropertyOperand property(String name) {
		return PropertyOperand.create(this, name);
	}
	
	default PropertyOperand property(Property property) {
		return PropertyOperand.create(this, property.getName());
	}
}

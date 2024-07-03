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
package com.braintribe.model.processing.query.building;

import java.util.Objects;
import java.util.function.Supplier;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.query.From;
import com.braintribe.model.query.Join;
import com.braintribe.model.query.PropertyOperand;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.Source;

public abstract class SelectQueries extends Queries implements Supplier<SelectQuery> {
	protected SelectQuery supply;
	
	public static PropertyOperand property(Source source, String name) {
		return source.property(name);
	}
	
	public static PropertyOperand property(Source source, Property property) {
		return source.property(property);
	}
	
	public static From source(EntityType<?> entityType) {
		return source(entityType.getTypeSignature(), entityType.getShortName());
	}
	
	public static From source(EntityType<?> entityType, String name) {
		return source(entityType.getTypeSignature(), name);
	}
	
	public static From source(String entityTypeSignature, String name) {
		From from = From.T.create();
		from.setEntityTypeSignature(entityTypeSignature);
		from.setName(name);
		
		return from;
	}
	
	public static SelectQuery from(From... froms) {
		return SelectQuery.create(froms);
	}

	public static Join join(Source source, String propertyName) {
		return source.join(propertyName);
	}
	
	@Override
	public SelectQuery get() {
		Objects.requireNonNull(supply, "Not allowed to call get() before setting supply field");
		return supply;
	}

}
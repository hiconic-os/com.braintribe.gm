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
package com.braintribe.model.processing.findrefs.meta;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.processing.manipulator.api.PropertyReferenceAnalyzer;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/**
 * This is a a {@link PropertyReferenceAnalyzer} for accesses which support full querying flexibility when it comes to
 * polymorphism. This analyzer only considers properties on the level they are declared, so that all the references of
 * that property for the entire hierarchy are considered at the same time.
 * 
 * 
 * h3. Example Model:
 * 
 * {@code
 * abstract entity AbstractPerson
 * 		Address address; 
 * 
 * entity GreenPerson
 * 		String greenProperty
 * 
 * entity BluePerson
 * 		String blueProperty
 * }
 * 
 * Now we are going to delete an instance of Address, thus we analyze where it might be referenced from. The regular
 * {@link BasicPropertyReferenceAnalyzer} would give us a list of three properties:
 * <ul>
 * <li>AbstractPerson.address</li>
 * <li>GreenPerson.address</li>
 * <li>BluePerson.address</li>
 * </ul>
 * 
 * If however, we are dealing with an access that has the ability for any type to do the query on the abstract level, we
 * only need to consider the <code>AbstractPerson.address</code>, which is what this implementation would return.
 * 
 * @author peter.gazdik
 */
public class OptimizedPolymorphicPropertyReferenceAnalyzer extends BasicPropertyReferenceAnalyzer {

	public OptimizedPolymorphicPropertyReferenceAnalyzer(ModelOracle modelOracle) {
		super(modelOracle);
	}

	@Override
	protected boolean ignoreEntity(GmEntityType entityType) {
		return isEmpty(entityType.getProperties());
	}

	@Override
	protected boolean ignoreProperty(GmEntityType actualOwner, GmProperty property) {
		return property.getDeclaringType() != actualOwner;
	}

}

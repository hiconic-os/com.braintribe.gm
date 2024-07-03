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
package model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.itw.synthesis.gm.JvmEntityType;
import com.braintribe.model.processing.itw.synthesis.gm.experts.PropertyPathResolver;

/**
 * @author peter.gazdik
 */
public class Person_EntityType extends JvmEntityType<Person> {

	public static final Person_EntityType INSTANCE = new Person_EntityType();

	@Override
	public Person_plain createPlainRaw() {
		return new Person_plain();
		// GWT:
		// return new Person_gm(EmptyPai);
	}

	@Override
	public Person_gm createRaw() {
		return new Person_gm(false);
	}

	@Override
	public Class<Person_plain> plainClass() {
		return Person_plain.class;
	}

	@Override
	public Class<Person_gm> enhancedClass() {
		return Person_gm.class;
	}

	@Override
	public boolean isInstance(Object value) {
		return value instanceof Person;
	}

	@Override
	public String getSelectiveInformationFor(GenericEntity entity) {
		throw new UnsupportedOperationException("TODO");
	}

	/**
	 * Note that we actually only implement the toString(GenericEntity) method (which would normally be just a bridge method). The reason is
	 * that the specific method (in this case toString(Person)) is not accessible in any way, all the actual calls in the code are
	 * (obviously) done on the abstract type (EntityType<?>).
	 */
	@Override
	@Deprecated
	public String toString(Person entity) {
		// "Prefix_${name}_${father.name}_Suffix"
		return "Prefix_" + PropertyPathResolver.resolvePropertyPath(entity, new Property[] { Person_Name_Property.INSTANCE }) + "_" +
				PropertyPathResolver.resolvePropertyPath(entity,
						new Property[] { Person__FATHER.INSTANCE, Person_Name_Property.INSTANCE }) +
				"_" + entity.getId() + "_Suffix";
	}

}

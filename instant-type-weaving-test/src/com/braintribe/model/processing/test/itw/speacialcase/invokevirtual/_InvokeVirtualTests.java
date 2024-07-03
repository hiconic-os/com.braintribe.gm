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
package com.braintribe.model.processing.test.itw.speacialcase.invokevirtual;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.ImportantItwTestSuperType;

/**
 * 
 */
public class _InvokeVirtualTests extends ImportantItwTestSuperType {

	@Test
	public void testInvokeVirutalWithMultipleInheritance() {
		Sub sub = enhanced(Sub.class);

		sub.setProp1("val1");
		sub.setProp2("val2");

		Property p1 = typeReflection().getEntityType(Super1.class).getProperty("prop1");
		Property p2 = typeReflection().getEntityType(Super2.class).getProperty("prop2");

		assertThat((String) p1.get(sub)).isEqualTo("val1");
		assertThat((String) p2.get(sub)).isEqualTo("val2");
	}

	@Test
	public void testInvokeVirutalWithMultipleInheritance_AbsenceInformation() {
		NamedAbsenceInformation ai1 = NamedAbsenceInformation.T.create();
		NamedAbsenceInformation ai2 = NamedAbsenceInformation.T.create();

		ai1.setName("ai1");
		ai2.setName("ai2");

		Sub sub = enhanced(Sub.class);

		Property p1 = typeReflection().getEntityType(Super1.class).getProperty("prop1");
		Property p2 = typeReflection().getEntityType(Super2.class).getProperty("prop2");

		p1.setAbsenceInformation(sub, ai1);
		p2.setAbsenceInformation(sub, ai2);
		
		assertThat(p1.getAbsenceInformation(sub)).isSameAs(ai1);
		assertThat(p2.getAbsenceInformation(sub)).isSameAs(ai2);
	}

	
	@ToStringInformation("NamedAbsenceInformation(${name})")
	public static interface NamedAbsenceInformation extends AbsenceInformation {
		EntityType<_InvokeVirtualTests.NamedAbsenceInformation> T = EntityTypes.T(NamedAbsenceInformation.class);
		
		String getName();
		void setName(String value);
	}

	protected <T extends GenericEntity> T enhanced(Class<T> beanClass) {
		EntityType<GenericEntity> entityType = typeReflection().getEntityType(beanClass);
		return beanClass.cast(entityType.create());
	}

	private static GenericModelTypeReflection typeReflection() {
		return GMF.getTypeReflection();
	}
}

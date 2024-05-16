// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.test.itw;

import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertEntity;
import static com.braintribe.utils.lcd.CollectionTools2.first;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmLongType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.override.GmPropertyOverride;
import com.braintribe.model.processing.ImportantItwTestSuperType;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysis;
import com.braintribe.model.processing.test.itw.entity.CovariantOverrideGetterEntity;
import com.braintribe.model.util.meta.NewMetaModelGeneration;
import com.braintribe.utils.junit.assertions.BtAssertions;

/**
 * @see CovariantOverrideGetterEntity
 */
public class CovariantOverrideGetterTests extends ImportantItwTestSuperType {

	@Test
	public void typeOverrideIsSetOnMetaModel() {
		JavaTypeAnalysis jta = new JavaTypeAnalysis();
		GmEntityType gmType = (GmEntityType) jta.getGmType(CovariantOverrideGetterEntity.class);

		List<GmPropertyOverride> propOverrides = gmType.getPropertyOverrides();
		assertEntity(gmType);
		assertThat(propOverrides).hasSize(1);

		GmPropertyOverride po = first(propOverrides);
		assertThat(po.getProperty().getName()).isEqualTo(GenericEntity.id);

		GmType to = po.getTypeOverride();
		assertEntity(to).isInstanceOf(GmLongType.T);
	}

	@Test
	public void typeOverrideIsWoven() {
		CovariantOverrideGetterEntity e = CovariantOverrideGetterEntity.T.create();
		e.setId(125L);

		Long longId = e.getId();
		assertThat(longId).isEqualTo(125L);
	}

	@Test
	public void checkWithModelOnlyEntity() throws Exception {
		GmMetaModel gmMetaModel = getAltModel();

		gmMetaModel.deploy();

		EntityType<?> et = GMF.getTypeReflection().getEntityType(alterSignature(CovariantOverrideGetterEntity.class.getName()));

		Method getIdMethod = et.getJavaType().getDeclaredMethod("getId");
		BtAssertions.assertThat(getIdMethod).isNotNull();
		BtAssertions.assertThat(getIdMethod.getReturnType()).isEqualTo(Long.class);
	}

	private GmMetaModel getAltModel() {
		JavaTypeAnalysis jta = new JavaTypeAnalysis();

		GmEntityType gmType = (GmEntityType) jta.getGmType(CovariantOverrideGetterEntity.class);
		String altSignature = alterSignature(gmType.getTypeSignature());
		gmType.setTypeSignature(altSignature);

		GmMetaModel gmMetaModel = new NewMetaModelGeneration().buildMetaModel("gm:ItwTest", emptySet());
		gmMetaModel.getTypes().add(gmType);

		return gmMetaModel;
	}

	private String alterSignature(String typeSignature) {
		return typeSignature.replace(".itw.", ".itw.alt.");
	}

}

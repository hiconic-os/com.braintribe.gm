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
package com.braintribe.model.access.security.testdata.manipulation;

import static com.braintribe.model.access.security.testdata.MdFactory.administrable;
import static com.braintribe.model.access.security.testdata.MdFactory.entityDeletionDisabled;
import static com.braintribe.model.access.security.testdata.MdFactory.instantiationDisabled;
import static com.braintribe.model.access.security.testdata.MdFactory.mandatory;
import static com.braintribe.model.access.security.testdata.MdFactory.nonModifiable;
import static com.braintribe.model.access.security.testdata.MdFactory.unique;
import static com.braintribe.model.access.security.testdata.MdFactory.useCaseSelector;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.List;

import com.braintribe.model.access.security.common.AbstractSecurityAspectTest;
import com.braintribe.model.access.security.testdata.query.AclEntity;
import com.braintribe.model.access.security.testdata.query.AclPropsOwner;
import com.braintribe.model.acl.Acl;
import com.braintribe.model.acl.HasAcl;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.selector.UseCaseSelector;
import com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor;
import com.braintribe.model.processing.meta.editor.ModelMetaDataEditor;
import com.braintribe.model.util.meta.NewMetaModelGeneration;

public class SecurityAspectManipulationTestModel {

	// @formatter:off
	public static final List<EntityType<?>> types = asList(
			AclEntity.T,
			AclPropsOwner.T,

			EntityWithConstraints.T,
			EntityWithPropertyConstraints.T
	);
	// @formatter:on

	private static final GmMetaModel accessControlModel = Acl.T.getModel().getMetaModel();

	public static GmMetaModel enriched() {
		GmMetaModel rawModel = new NewMetaModelGeneration().buildMetaModel("gm:security-manipulation-test-model", types, asList(accessControlModel));

		ModelMetaDataEditor mdEditor = new BasicModelMetaDataEditor(rawModel);

		mdEditor.onEntityType(EntityWithPropertyConstraints.T) //
				.addPropertyMetaData("mandatory", mandatory()) //
				.addPropertyMetaData("unique", unique()) //
				.addPropertyMetaData("uniqueEntity", unique()) //
				.addPropertyMetaData("nonModifiable", nonModifiable()) //
				.addPropertyMetaData("nonModifiableButMandatory", nonModifiable(), mandatory()) //
		;

		mdEditor.onEntityType(EntityWithConstraints.T) //
				.addMetaData(instantiationDisabled(), entityDeletionDisabled());

		// ####################################
		// ## . . . . . . ACL . . . . . . . .##
		// ####################################

		UseCaseSelector aclUseCase = useCaseSelector("acl");

		mdEditor.onEntityType(Acl.T) //
				.addMetaData(administrable(AbstractSecurityAspectTest.ADMINISTERING_ACL_ROLE, aclUseCase));

		mdEditor.onEntityType(HasAcl.T) //
				.addMetaData(administrable(AbstractSecurityAspectTest.ADMINISTERING_HAS_ACL_ROLE, aclUseCase));

		return rawModel;
	}

}

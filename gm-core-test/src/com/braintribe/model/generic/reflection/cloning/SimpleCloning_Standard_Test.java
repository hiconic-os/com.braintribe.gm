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
package com.braintribe.model.generic.reflection.cloning;

import org.junit.Test;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.cloning.model.City;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.session.impl.notifying.BasicNotifyingGmSession;

/**
 * @author peter.gazdik
 */
public class SimpleCloning_Standard_Test extends SimpleCloning_Base {

	@Test
	@Override
	public void simplyCopying() {
		cc = new StandardCloningContext();

		runSimplyCopying();
	}

	@Test
	@Override
	public void copyOnASession() {
		GmSession session = new BasicNotifyingGmSession();

		cc = new StandardCloningContext() {
			@Override
			public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
				return session.create(entityType);
			}
		};

		runCopyOnASession(session);
	}

	@Test
	@Override
	public void doNotCopyIdStuff() {
		cc = new StandardCloningContext() {
			@Override
			public boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property,
					GenericEntity instanceToBeCloned, GenericEntity clonedInstance, AbsenceInformation sourceAbsenceInformation) {

				return !property.isIdentifying() && !property.isGlobalId();
			}
		};

		runDoNotCopyIdStuff();
	}

	@Test
	@Override
	public void referenceOriginalPropertyValue() {
		cc = new StandardCloningContext() {
			@Override
			public <T> T getAssociated(GenericEntity e) {
				return e instanceof City ? (T) e : super.getAssociated(e);
			}
		};

		runReferenceOriginalPropertyValue();
	}

	@Test
	@Override
	public void stringifyIdInPreProcess() {
		cc = new StandardCloningContext() {
			@Override
			public GenericEntity preProcessInstanceToBeCloned(GenericEntity instanceToBeCloned) {
				return stringifyId(instanceToBeCloned);
			}
		};

		runStringifyIdInPreProcess();
	}

	@Test
	@Override
	public void stringifyIdInPostProcess() {
		cc = new StandardCloningContext() {
			@Override
			public Object postProcessCloneValue(GenericModelType propertyOrElementType, Object o) {
				return o instanceof Long ? "" + o : o;
			}
		};

		runStringifyIdInPostProcess();
	}

}

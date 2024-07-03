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
package com.braintribe.model.processing.cmd.test.provider;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.EnumConstantMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.cmd.test.meta.ActivableMetaData;
import com.braintribe.model.processing.cmd.test.meta.EntityRelatedMetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.enumeration.SimpleEnumConstantMetaData;
import com.braintribe.model.processing.cmd.test.meta.property.SimplePropertyMetaData;
import com.braintribe.model.processing.cmd.test.model.Color;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.model.Teacher;

/**
 * 
 */
public class ImportantMdProvider extends AbstractModelSupplier {

	@Override
	protected void addMetaData() {
		addImportantEntityMd();
		addImportantPropertyMd();
		addImportantPropertyMdWithLowPrio();
		addImportantEnumConstantMd();
	}

	/**
	 * The important MD defined for super-type has higher conflictPriority than the lower one, so it should be taken (if it was not important, the
	 * priority would not matter.)
	 */
	private void addImportantEntityMd() {
		fullMdEditor.onEntityType(Person.T) //
				.addMetaData(newMd(SimpleEntityMetaData.T, Person.T, 10.0d, true));
		fullMdEditor.onEntityType(Teacher.T) //
				.addMetaData(newMd(SimpleEntityMetaData.T, Teacher.T, 0.0d, false));
	}

	/**
	 * Same as {@link #addImportantEntityMd()}
	 */
	private void addImportantPropertyMd() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("age", newMd(SimplePropertyMetaData.T, Person.T, 10.0d, true));
		fullMdEditor.onEntityType(Teacher.T) //
				.addPropertyMetaData("age", newMd(SimplePropertyMetaData.T, Teacher.T, 0.0d, false));
	}

	/**
	 * Similar to {@link #addImportantPropertyMd()}, but the important MD has lower priority than the local one.
	 */
	private void addImportantPropertyMdWithLowPrio() {
		fullMdEditor.onEntityType(Person.T) //
				.addPropertyMetaData("name", newMd(SimplePropertyMetaData.T, Person.T, -10.0d, true));
		fullMdEditor.onEntityType(Teacher.T) //
				.addPropertyMetaData("name", newMd(SimplePropertyMetaData.T, Teacher.T, 0.0d, false));
	}

	public static <M extends MetaData & ActivableMetaData & EntityRelatedMetaData> M newMd(EntityType<M> mdEt, EntityType<?> entityType,
			double priority, boolean important) {
		M amd = newMd(mdEt, true, entityType);
		amd.setConflictPriority(priority);
		amd.setImportant(important);

		return amd;
	}

	/**
	 * Similar to {@link #addImportantPropertyMd()}, but the important MD has lower priority than the local one.
	 */
	private void addImportantEnumConstantMd() {
		fullMdEditor //
				.addConstantMetaData(Color.class, newMd(SimpleEnumConstantMetaData.T, 10.0d, true)) //
				.addConstantMetaData(Color.GREEN, newMd(SimpleEnumConstantMetaData.T, 0.0d, false));
	}

	private static <M extends EnumConstantMetaData & ActivableMetaData> M newMd(EntityType<M> mdEt, double priority, boolean important) {

		M amd = newMd(mdEt, important);
		amd.setConflictPriority(priority);
		amd.setImportant(important);

		return amd;
	}
}

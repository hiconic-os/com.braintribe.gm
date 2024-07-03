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

import com.braintribe.model.processing.cmd.test.meta.entity.SimpleEntityMetaData;
import com.braintribe.model.processing.cmd.test.meta.entity.SimpleInheritedMetaData;
import com.braintribe.model.processing.cmd.test.model.Person;
import com.braintribe.model.processing.cmd.test.model.ServiceProvider;
import com.braintribe.model.processing.cmd.test.model.Teacher;

/**
 * Tests whether the caching is working OK, especially that after finding a static meta-data, the resolver does not try to look any further no matter
 * what.
 */
public class ExclusiveMdProvider extends AbstractModelSupplier {

	@Override
	protected void addMetaData() {
		addSimpleExclusiveMd();
		addInheritedExclusiveMd();
	}

	/**
	 * The purpose is to have two meta-data, the non-static one with lower priority.
	 */
	private void addSimpleExclusiveMd() {
		fullMdEditor.onEntityType(Teacher.T).addMetaData( //
				append(newMd(SimpleEntityMetaData.T, true), 10), //
				append(newMd(SimpleEntityMetaData.T, false), unreachable(), 0) //
		);

		fullMdEditor.onEntityType(Person.T) //
				.addMetaData(append(newMd(SimpleEntityMetaData.T, false), unreachable()));
	}

	/**
	 * The purpose is to have two meta-data, the non-static in first super-type, so that second super-type won't be visited. Note that first
	 * super-type of {@link Teacher} is {@link Person}, then comes {@link ServiceProvider}.
	 */
	private void addInheritedExclusiveMd() {
		fullMdEditor.onEntityType(Person.T) //
				.addMetaData(newMd(SimpleInheritedMetaData.T, true, Person.T));
		fullMdEditor.onEntityType(ServiceProvider.T) //
				.addMetaData(append(newMd(SimpleInheritedMetaData.T, false, ServiceProvider.T), unreachable()));
	}

}

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
package com.braintribe.model.dbs;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import java.util.Set;


import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Mandatory;

@SelectiveInformation("${name}")
public interface DbSchema extends IdentifiableDbEntity {

	EntityType<DbSchema> T = EntityTypes.T(DbSchema.class);

	// @formatter:off
	@Mandatory
	String getName();
	void setName(String name);

	Set<DbTable> getTables();
	void setTables(Set<DbTable> tables);

	Set<DbSequence> getSequences();
	void setSequences(Set<DbSequence> sequences);
	// @formatter:on

}

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
package com.braintribe.model.generic.mdec;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@ForwardDeclaration("com.braintribe.gm:model-declaration-model")
public interface ModelDeclaration extends GenericEntity {

	final EntityType<ModelDeclaration> T = EntityTypes.T(ModelDeclaration.class);

	void setArtifactId(String artifactId);
	String getArtifactId();

	void setGroupId(String groupId);
	String getGroupId();

	void setVersion(String version);
	String getVersion();

	void setName(String name);
	String getName();

	void setModelGlobalId(String modelGlobalId);
	String getModelGlobalId();

	void setHash(String hash);
	String getHash();

	void setTypes(Set<String> types);
	Set<String> getTypes();

	void setDependencies(List<ModelDeclaration> dependencies);
	List<ModelDeclaration> getDependencies();

}

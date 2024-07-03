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
package com.braintribe.model.template;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.template.meta.TemplateMetaData;

@SelectiveInformation("${technicalName}")
public interface Template extends GenericEntity {

	EntityType<Template> T = EntityTypes.T(Template.class);

	String getTechnicalName();
	void setTechnicalName(String technicalName);

	LocalizedString getName();
	void setName(LocalizedString name);

	LocalizedString getDescription();
	void setDescription(LocalizedString description);

	Object getPrototype();
	void setPrototype(Object prototype);

	String getPrototypeTypeSignature();
	void setPrototypeTypeSignature(String prototypeTypeSignature);

	Manipulation getScript();
	void setScript(Manipulation script);

	Set<TemplateMetaData> getMetaData();
	void setMetaData(Set<TemplateMetaData> metaData);

}

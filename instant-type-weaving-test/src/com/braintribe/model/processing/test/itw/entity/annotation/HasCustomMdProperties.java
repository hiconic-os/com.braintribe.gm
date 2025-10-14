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
package com.braintribe.model.processing.test.itw.entity.annotation;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface HasCustomMdProperties extends GenericEntity {

	EntityType<HasCustomMdProperties> T = EntityTypes.T(HasCustomMdProperties.class);

	@CustomPredicate_Annotation(globalId = "gid-predicate")
	String getPredicate();
	void setPredicate(String predicate);

	@CustomMd_Annotation( //
			value = 182, //
			inherited = false, //
			important = true, //
			name = "CUSTOM_NAME", //
			customEnum = CustomMdEnum.bbb, //
			customEnumList = { CustomMdEnum.aaa, CustomMdEnum.bbb }, //
			customEnumSet = { CustomMdEnum.aaa, CustomMdEnum.ccc } //
	)
	String getCustomMd();
	void setCustomMd(String customMd);

	@CustomRepeatableMd_Annotation("one")
	String getSingleRepeatableMd();
	void setSingleRepeatableMd(String singleRepeatableMd);

	@CustomRepeatableMd_Annotation("one")
	@CustomRepeatableMd_Annotation("two")
	String getTwoRepeatableMds();
	void setTwoRepeatableMds(String twoRepeatableMds);

}

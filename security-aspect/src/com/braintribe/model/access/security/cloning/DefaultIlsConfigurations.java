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
package com.braintribe.model.access.security.cloning;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Set;

import com.braintribe.model.access.security.cloning.experts.AclEntityAccessExpert;
import com.braintribe.model.access.security.cloning.experts.EntityVisibilityIlsExpert;
import com.braintribe.model.access.security.cloning.experts.PropertyVisibilityIlsExpert;
import com.braintribe.model.generic.typecondition.TypeCondition;
import com.braintribe.model.generic.typecondition.basic.IsAnyType;
import com.braintribe.model.processing.security.query.PostQueryExpertConfiguration;
import com.braintribe.model.processing.security.query.expert.PostQueryExpert;

/**
 * 
 */
public class DefaultIlsConfigurations {

	private static final TypeCondition ANY_TYPE = IsAnyType.T.create();
	private static final Set<PostQueryExpertConfiguration> configs;

	static {
		configs = asSet( //
				config(new AclEntityAccessExpert()), //
				config(new EntityVisibilityIlsExpert()), //
				config(new PropertyVisibilityIlsExpert()) //
		);
	}

	private static PostQueryExpertConfiguration config(PostQueryExpert expert) {
		PostQueryExpertConfiguration result = new PostQueryExpertConfiguration();

		result.setExpert(expert);
		result.setTypeCondition(ANY_TYPE);

		return result;
	}

	public static Set<PostQueryExpertConfiguration> get() {
		return configs;
	}

}

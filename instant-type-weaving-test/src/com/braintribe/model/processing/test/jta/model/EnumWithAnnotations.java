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
package com.braintribe.model.processing.test.jta.model;

import com.braintribe.model.generic.annotation.meta.Color;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;

@Description("enum description")
public enum EnumWithAnnotations implements com.braintribe.model.generic.base.EnumBase {

	@Name("C1")
	@Name(locale = "de", value = "C1-DE")
	@Description("constant description")
	@Color("red")
	constant1;

	public static final EnumType T = EnumTypes.T(EnumWithAnnotations.class);

	@Override
	public EnumType type() {
		return T;
	}

}

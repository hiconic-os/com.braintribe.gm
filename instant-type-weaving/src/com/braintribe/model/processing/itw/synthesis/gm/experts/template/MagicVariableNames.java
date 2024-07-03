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
package com.braintribe.model.processing.itw.synthesis.gm.experts.template;

import static com.braintribe.model.processing.core.commons.SelectiveInformationSupport.SI_ID;
import static com.braintribe.model.processing.core.commons.SelectiveInformationSupport.SI_RUNTIME_ID;
import static com.braintribe.model.processing.core.commons.SelectiveInformationSupport.SI_TYPE;
import static com.braintribe.model.processing.core.commons.SelectiveInformationSupport.SI_TYPE_ALT;
import static com.braintribe.model.processing.core.commons.SelectiveInformationSupport.SI_TYPE_SHORT;

public class MagicVariableNames {

	public enum MagicVarKind {
		none,
		var_type,
		var_type_short,
		var_id,
		var_runtimeId
	}

	public static MagicVarKind getMagicVarKind(String varName) {
		switch (varName) {
			case SI_TYPE:
			case SI_TYPE_ALT:
				return MagicVarKind.var_type;
			case SI_TYPE_SHORT:
				return MagicVarKind.var_type_short;
			case SI_ID:
				return MagicVarKind.var_id;
			case SI_RUNTIME_ID:
				return MagicVarKind.var_runtimeId;
			default:
				return MagicVarKind.none;
		}
	}

}

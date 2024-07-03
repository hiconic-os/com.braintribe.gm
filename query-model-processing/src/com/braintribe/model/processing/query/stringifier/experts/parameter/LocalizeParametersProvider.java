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
package com.braintribe.model.processing.query.stringifier.experts.parameter;

import java.util.Arrays;
import java.util.Collection;

import com.braintribe.model.processing.query.api.stringifier.experts.paramter.MultipleParameterProvider;
import com.braintribe.model.query.functions.Localize;

public class LocalizeParametersProvider implements MultipleParameterProvider<Localize> {
	@Override
	public Collection<?> provideParameters(Localize function) {
		return Arrays.asList(function.getLocalizedStringOperand(), function.getLocale());
	}
}

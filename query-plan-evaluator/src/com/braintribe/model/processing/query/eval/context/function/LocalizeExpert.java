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
package com.braintribe.model.processing.query.eval.context.function;

import java.util.Map;

import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.processing.query.eval.api.QueryEvaluationContext;
import com.braintribe.model.processing.query.eval.api.Tuple;
import com.braintribe.model.processing.query.eval.api.function.QueryFunctionExpert;
import com.braintribe.model.processing.query.eval.api.function.aspect.LocaleQueryAspect;
import com.braintribe.model.query.functions.Localize;
import com.braintribe.model.queryplan.value.Value;
import com.braintribe.utils.i18n.I18nTools;

/**
 * 
 */
public class LocalizeExpert implements QueryFunctionExpert<Localize> {

	public static final LocalizeExpert INSTANCE = new LocalizeExpert();

	private LocalizeExpert() {
	}

	@Override
	public Object evaluate(Tuple tuple, Localize queryFunction, Map<Object, Value> operandMappings, QueryEvaluationContext context) {
		LocalizedString ls = context.resolveValue(tuple, operandMappings.get(queryFunction.getLocalizedStringOperand()));
		if (ls == null)
			return null;

		String locale = getLocale(queryFunction, context);

		return I18nTools.get(ls, locale);
	}

	private String getLocale(Localize queryFunction, QueryEvaluationContext context) {
		String locale = queryFunction.getLocale();
		if (locale != null)
			return locale;

		return context.getFunctionAspect(LocaleQueryAspect.class);
	}

}

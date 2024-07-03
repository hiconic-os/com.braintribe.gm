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
package com.braintribe.model.processing.vde.evaluator.impl.bvd.string;

import com.braintribe.model.bvd.string.Localize;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.processing.vde.evaluator.api.ValueDescriptorEvaluator;
import com.braintribe.model.processing.vde.evaluator.api.VdeContext;
import com.braintribe.model.processing.vde.evaluator.api.VdeResult;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;
import com.braintribe.model.processing.vde.evaluator.impl.VdeResultImpl;
import com.braintribe.utils.i18n.I18nTools;

/**
 * {@link ValueDescriptorEvaluator} for {@link Localize}
 * 
 */
public class LocalizeVde implements ValueDescriptorEvaluator<Localize> {

	@Override
	public VdeResult evaluate(VdeContext context, Localize valueDescriptor) throws VdeRuntimeException {

		Object localeObject = valueDescriptor.getLocale();
		Object localisedStringObject = valueDescriptor.getLocalizedString();

		if (localisedStringObject instanceof LocalizedString && localeObject instanceof String) {
			LocalizedString localisedString = (LocalizedString) localisedStringObject;
			String locale = (String) localeObject;
			return new VdeResultImpl(I18nTools.get(localisedString, locale), false);
		}
		throw new VdeRuntimeException("Localize is valid for LocalizedString and String and not :" + localisedStringObject + " " + localeObject);
	}

}

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
package com.braintribe.model.generic.annotation.meta.handlers;

import static com.braintribe.model.generic.annotation.meta.base.MdaAnalysisTools.newMd;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.generic.annotation.meta.AutoAlias;
import com.braintribe.model.generic.annotation.meta.AutoAliases;
import com.braintribe.model.generic.annotation.meta.WordCasing;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;
import com.braintribe.model.meta.data.mapping.WordSeparator;
import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
public class AutoAliasMdaHandler implements RepeatableMdaHandler<AutoAlias, AutoAliases, com.braintribe.model.meta.data.mapping.AutoAlias> {

	public static final AutoAliasMdaHandler INSTANCE = new AutoAliasMdaHandler();

	private final RepeatableAggregatorMdaHandler<AutoAliases, com.braintribe.model.meta.data.mapping.AutoAlias> aggregatorHandler = new BasicRepeatableAggregatorMdaHandler<>(
			AutoAliases.class, com.braintribe.model.meta.data.mapping.AutoAlias.class, this::buildMdListForRepeatable);

	// @formatter:off
	@Override public Class<AutoAlias> annotationClass() { return AutoAlias.class; }
	@Override public RepeatableAggregatorMdaHandler<AutoAliases, com.braintribe.model.meta.data.mapping.AutoAlias> aggregatorHandler() { return aggregatorHandler; }
	@Override public Class<com.braintribe.model.meta.data.mapping.AutoAlias> metaDataClass() { return com.braintribe.model.meta.data.mapping.AutoAlias.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.mapping.AutoAlias> buildMdList(AutoAlias annotation, MdaAnalysisContext context) {
		return buildMetaDataFor(context, annotation);
	}

	private List<com.braintribe.model.meta.data.mapping.AutoAlias> buildMdListForRepeatable(AutoAliases aliases, MdaAnalysisContext context) {
		return buildMetaDataFor(context, aliases.value());
	}

	private static List<com.braintribe.model.meta.data.mapping.AutoAlias> buildMetaDataFor(MdaAnalysisContext context, AutoAlias... aliases) {
		List<com.braintribe.model.meta.data.mapping.AutoAlias> result = newList();

		int i = 0;
		for (AutoAlias alias : aliases)
			result.add(toAliasMd(context, alias, i++));

		return result;
	}

	private static com.braintribe.model.meta.data.mapping.AutoAlias toAliasMd(MdaAnalysisContext context, AutoAlias alias, int i) {
		String globalId = alias.globalId();

		com.braintribe.model.meta.data.mapping.AutoAlias result = newMd(context, com.braintribe.model.meta.data.mapping.AutoAlias.T, globalId, i);
		result.setCasing(convertCasing(alias.casing()));
		result.setSeparator(convertSeparator(alias.separator()));

		return result;
	}

	private static com.braintribe.model.meta.data.mapping.WordCasing convertCasing(WordCasing casing) {
		if (casing == null)
			return com.braintribe.model.meta.data.mapping.WordCasing.original;

		switch (casing) {
			// @formatter:off
			case original: return com.braintribe.model.meta.data.mapping.WordCasing.original;
			case lower: return com.braintribe.model.meta.data.mapping.WordCasing.lower;
			case upper: return com.braintribe.model.meta.data.mapping.WordCasing.upper;
			default: throw new UnknownEnumException(casing);
			// @formatter:on
		}
	}

	private static WordSeparator convertSeparator(com.braintribe.model.generic.annotation.meta.WordSeparator separator) {
		if (separator == null)
			return com.braintribe.model.meta.data.mapping.WordSeparator.none;

		switch (separator) {
			// @formatter:off
			case none: return com.braintribe.model.meta.data.mapping.WordSeparator.none;
			case underscore: return com.braintribe.model.meta.data.mapping.WordSeparator.underscore;
			case hyphen: return com.braintribe.model.meta.data.mapping.WordSeparator.hyphen;
			default: throw new UnknownEnumException(separator);
			// @formatter:on
		}
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.mapping.AutoAlias md) {
		SingleAnnotationDescriptor result = context.newDescriptor(AutoAlias.class);
		result.addAnnotationValue("casing", NullSafe.get(md.getCasing(), WordCasing.original).name());
		result.addAnnotationValue("separator", NullSafe.get(md.getSeparator(), WordSeparator.none).name());

		context.setCurrentDescriptorMulti(result, AutoAliases.class);
	}

}

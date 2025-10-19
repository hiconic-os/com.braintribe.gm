// ============================================================================
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
import com.braintribe.model.generic.annotation.meta.AliasRule;
import com.braintribe.model.generic.annotation.meta.AliaseRules;
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
public class AliasRuleMdaHandler implements RepeatableMdaHandler<AliasRule, AliaseRules, com.braintribe.model.meta.data.mapping.AliasRule> {

	public static final AliasRuleMdaHandler INSTANCE = new AliasRuleMdaHandler();

	private final RepeatableAggregatorMdaHandler<AliaseRules, com.braintribe.model.meta.data.mapping.AliasRule> aggregatorHandler = new BasicRepeatableAggregatorMdaHandler<>(
			AliaseRules.class, com.braintribe.model.meta.data.mapping.AliasRule.class, this::buildMdListForRepeatable);

	// @formatter:off
	@Override public Class<AliasRule> annotationClass() { return AliasRule.class; }
	@Override public RepeatableAggregatorMdaHandler<AliaseRules, com.braintribe.model.meta.data.mapping.AliasRule> aggregatorHandler() { return aggregatorHandler; }
	@Override public Class<com.braintribe.model.meta.data.mapping.AliasRule> metaDataClass() { return com.braintribe.model.meta.data.mapping.AliasRule.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.mapping.AliasRule> buildMdList(AliasRule annotation, MdaAnalysisContext context) {
		return buildMetaDataFor(context, annotation);
	}

	private List<com.braintribe.model.meta.data.mapping.AliasRule> buildMdListForRepeatable(AliaseRules aliases, MdaAnalysisContext context) {
		return buildMetaDataFor(context, aliases.value());
	}

	private static List<com.braintribe.model.meta.data.mapping.AliasRule> buildMetaDataFor(MdaAnalysisContext context, AliasRule... aliases) {
		List<com.braintribe.model.meta.data.mapping.AliasRule> result = newList();

		int i = 0;
		for (AliasRule alias : aliases)
			result.add(toAliasMd(context, alias, i++));

		return result;
	}

	private static com.braintribe.model.meta.data.mapping.AliasRule toAliasMd(MdaAnalysisContext context, AliasRule alias, int i) {
		String globalId = alias.globalId();

		com.braintribe.model.meta.data.mapping.AliasRule result = newMd(context, com.braintribe.model.meta.data.mapping.AliasRule.T, globalId, i);
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
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.mapping.AliasRule md) {
		SingleAnnotationDescriptor result = context.newDescriptor(AliasRule.class);
		result.addAnnotationValue("casing", NullSafe.get(md.getCasing(), WordCasing.original).name());
		result.addAnnotationValue("separator", NullSafe.get(md.getSeparator(), WordSeparator.none).name());

		context.setCurrentDescriptorMulti(result, AliaseRules.class);
	}

}

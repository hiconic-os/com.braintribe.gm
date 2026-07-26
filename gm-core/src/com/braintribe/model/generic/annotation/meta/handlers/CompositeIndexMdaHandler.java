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
import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.model.generic.annotation.meta.CompositeIndex;
import com.braintribe.model.generic.annotation.meta.CompositeIndices;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;

/**
 * @author peter.gazdik
 */
public class CompositeIndexMdaHandler
		implements RepeatableMdaHandler<CompositeIndex, CompositeIndices, com.braintribe.model.meta.data.query.CompositeIndex> {

	public static final CompositeIndexMdaHandler INSTANCE = new CompositeIndexMdaHandler();

	private final RepeatableAggregatorMdaHandler<CompositeIndices, com.braintribe.model.meta.data.query.CompositeIndex> aggregatorHandler = new BasicRepeatableAggregatorMdaHandler<>(
			CompositeIndices.class, com.braintribe.model.meta.data.query.CompositeIndex.class, this::buildMdListForRepeatable);

	// @formatter:off
	@Override public Class<CompositeIndex> annotationClass() { return CompositeIndex.class; }
	@Override public RepeatableAggregatorMdaHandler<CompositeIndices, com.braintribe.model.meta.data.query.CompositeIndex> aggregatorHandler() { return aggregatorHandler; }
	@Override public Class<com.braintribe.model.meta.data.query.CompositeIndex> metaDataClass() { return com.braintribe.model.meta.data.query.CompositeIndex.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.query.CompositeIndex> buildMdList(CompositeIndex annotation, MdaAnalysisContext context) {
		return buildMetaDataFor(context, annotation);
	}

	private List<com.braintribe.model.meta.data.query.CompositeIndex> buildMdListForRepeatable(CompositeIndices indices, MdaAnalysisContext context) {
		return buildMetaDataFor(context, indices.value());
	}

	private static List<com.braintribe.model.meta.data.query.CompositeIndex> buildMetaDataFor(MdaAnalysisContext context, CompositeIndex... indices) {
		List<com.braintribe.model.meta.data.query.CompositeIndex> result = newList();

		int i = 0;
		for (CompositeIndex index : indices)
			result.add(toAliasMd(context, index, i++));

		return result;
	}

	private static com.braintribe.model.meta.data.query.CompositeIndex toAliasMd(MdaAnalysisContext context, CompositeIndex alias, int i) {
		String globalId = alias.globalId();

		com.braintribe.model.meta.data.query.CompositeIndex result = newMd(context, com.braintribe.model.meta.data.query.CompositeIndex.T, globalId,
				i);
		result.getPropertyNames().addAll(asList(alias.value()));

		return result;
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.query.CompositeIndex md) {
		List<String> propNames = md.getPropertyNames();

		SingleAnnotationDescriptor result = context.newDescriptor(CompositeIndex.class);
		result.addAnnotationValue("value", propNames.toArray(new String[propNames.size()]));

		context.setCurrentDescriptorMulti(result, CompositeIndices.class);
	}

}

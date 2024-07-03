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
import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Comparator;
import java.util.List;

import com.braintribe.model.generic.annotation.meta.CompoundUnique;
import com.braintribe.model.generic.annotation.meta.CompoundUniques;
import com.braintribe.model.generic.annotation.meta.api.RepeatableMdaHandler;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.MdaSynthesisContext;
import com.braintribe.model.generic.annotation.meta.api.synthesis.SingleAnnotationDescriptor;
import com.braintribe.model.generic.annotation.meta.base.BasicRepeatableAggregatorMdaHandler;

/**
 * @author peter.gazdik
 */
public class CompoundUniqueMdaHandler
		implements RepeatableMdaHandler<CompoundUnique, CompoundUniques, com.braintribe.model.meta.data.constraint.CompoundUnique> {

	public static final CompoundUniqueMdaHandler INSTANCE = new CompoundUniqueMdaHandler();

	private final RepeatableAggregatorMdaHandler<CompoundUniques, com.braintribe.model.meta.data.constraint.CompoundUnique> repeatableHandler = new BasicRepeatableAggregatorMdaHandler<>(
			CompoundUniques.class, com.braintribe.model.meta.data.constraint.CompoundUnique.class, this::buildMdListForRepeatable);

	// @formatter:off
	@Override public Class<CompoundUnique> annotationClass() { return CompoundUnique.class; }
	@Override public RepeatableAggregatorMdaHandler<CompoundUniques, com.braintribe.model.meta.data.constraint.CompoundUnique> aggregatorHandler() { return repeatableHandler; }
	@Override public Class<com.braintribe.model.meta.data.constraint.CompoundUnique> metaDataClass() { return com.braintribe.model.meta.data.constraint.CompoundUnique.class; }
	// @formatter:on

	@Override
	public List<com.braintribe.model.meta.data.constraint.CompoundUnique> buildMdList(CompoundUnique cu, MdaAnalysisContext context) {
		return buildMd(context, cu);
	}

	private List<com.braintribe.model.meta.data.constraint.CompoundUnique> buildMdListForRepeatable(CompoundUniques compoundUniques,
			MdaAnalysisContext context) {
		return buildMd(context, compoundUniques.value());
	}

	private static List<com.braintribe.model.meta.data.constraint.CompoundUnique> buildMd(MdaAnalysisContext context, CompoundUnique... cus) {
		List<com.braintribe.model.meta.data.constraint.CompoundUnique> result = newList();

		int i = 0;
		for (CompoundUnique cu : cus)
			result.add(toCuMd(context, cu, i++));

		return result;
	}

	private static com.braintribe.model.meta.data.constraint.CompoundUnique toCuMd(MdaAnalysisContext context, CompoundUnique cu, int i) {
		String globalId = cu.globalId();

		com.braintribe.model.meta.data.constraint.CompoundUnique result = newMd(context, com.braintribe.model.meta.data.constraint.CompoundUnique.T,
				globalId, i);

		result.setUniqueProperties(asSet(cu.value()));
		return result;
	}

	@Override
	public void buildAnnotation(MdaSynthesisContext context, com.braintribe.model.meta.data.constraint.CompoundUnique md) {
		List<String> up = newList(md.getUniqueProperties());
		up.sort(Comparator.naturalOrder());

		SingleAnnotationDescriptor result = context.newDescriptor(CompoundUnique.class);
		result.addAnnotationValue("value", up.toArray(new String[up.size()]));

		context.setCurrentDescriptorMulti(result, CompoundUniques.class);
	}

}

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
package com.braintribe.model.processing.itw.analysis.meta;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;
import static com.braintribe.utils.lcd.StringTools.isEmpty;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.annotation.meta.api.MdaHandler;
import com.braintribe.model.generic.annotation.meta.api.MdaRegistry;
import com.braintribe.model.generic.annotation.meta.api.MetaDataAnnotations;
import com.braintribe.model.generic.annotation.meta.api.analysis.MdaAnalysisContext;
import com.braintribe.model.meta.data.HasMetaData;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.weaving.data.ProtoHasMetaData;

/**
 * @author peter.gazdik
 */
public class MetaDataAnnotationAnalyzers {

	private static final Logger log = Logger.getLogger(MetaDataAnnotationAnalyzers.class);

	/**
	 * @param annotations
	 *            array of all {@link Annotation}s to be analyzed.
	 * @param context
	 *            context containing {@link HasMetaData target} to which the {@link MetaData} are added. In case the
	 *            analyzed annotation has no globalId, a default one is created based on the globalId of the target.
	 */
	public static void analyzeMetaDataAnnotations(Annotation[] annotations, MdaAnalysisContext context) {
		ProtoHasMetaData target = context.getTarget();

		Objects.requireNonNull(target.getGlobalId(), () -> "Target must have a globalId. Target: " + target);

		MdaRegistry mdaRegistry = MetaDataAnnotations.registry();
		Map<Class<? extends Annotation>, MdaHandler<?, ?>> analyzerMap = context.isProto() ? mdaRegistry.protoAnnoToHandler()
				: mdaRegistry.annoToHandler();

		Set<MetaData> metaData = target.getMetaData();
		if (metaData == null) {
			metaData = newSet();
			target.setMetaData(metaData);
		}

		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();

			MdaHandler<Annotation, MetaData> analyzer = (MdaHandler<Annotation, MetaData>) analyzerMap.get(annotationType);
			if (analyzer == null)
				continue;

			int i = 1;
			List<MetaData> analyzedMd = analyzer.buildMdList(annotation, context);

			for (MetaData metaDatum : analyzedMd)
				if (metaDatum.getGlobalId() == null) {
					String globalId = "annotated:" + target.getGlobalId() + "/" + annotationType.getSimpleName();

					if (analyzedMd.size() > 1)
						globalId += "#" + i++;

					metaDatum.setGlobalId(globalId);
				}

			metaData.addAll(analyzedMd);
		}
	}

	/* package */ static void checkGlobalId(String globalId1, String globalId2, Class<? extends Annotation> annotationClass) {
		boolean twoDifferentValues = !isEmpty(globalId1) && !isEmpty(globalId2) && !globalId1.equals(globalId2);

		if (twoDifferentValues)
			log.warn("Different globalId configured for the same meta-data of type '" + annotationClass.getSimpleName() + "'. First: " + globalId1
					+ ", second: " + globalId2);
	}

}

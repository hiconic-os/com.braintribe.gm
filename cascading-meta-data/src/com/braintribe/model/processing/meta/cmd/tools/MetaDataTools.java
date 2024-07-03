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
package com.braintribe.model.processing.meta.cmd.tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

/**
 * 
 */
public class MetaDataTools {

	public static  List<QualifiedMetaData> prepareMetaDataForType(Collection<QualifiedMetaData> qmds, EntityType<? extends MetaData> type) {
		List<QualifiedMetaData> result = qmds.stream().filter(qmd -> type.isInstance(qmd.metaData())).collect(Collectors.toList());

		Collections.sort(result, MetaDataPriorityComparator.instance);

		return result;
	}

	public static class MetaDataPriorityComparator implements Comparator<QualifiedMetaData> {
		public static final MetaDataTools.MetaDataPriorityComparator instance = new MetaDataTools.MetaDataPriorityComparator();

		@Override
		public int compare(QualifiedMetaData md1, QualifiedMetaData md2) {
			double cp1 = toPriorityValue(md1.metaData().getConflictPriority());
			double cp2 = toPriorityValue(md2.metaData().getConflictPriority());

			return Double.compare(cp2, cp1);
		}

		private double toPriorityValue(Double conflictPriority) {
			return conflictPriority == null ? 0.0d : conflictPriority.doubleValue();
		}
	}

	@SafeVarargs
	public static Collection<Class<? extends SelectorContextAspect<?>>> aspects(Class<? extends SelectorContextAspect<?>>... classes) {
		return Arrays.asList(classes);
	}

}

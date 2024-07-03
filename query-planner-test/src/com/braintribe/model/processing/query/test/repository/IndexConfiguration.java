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
package com.braintribe.model.processing.query.test.repository;

import static com.braintribe.utils.lcd.CollectionTools2.acquireMap;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;
import com.braintribe.model.processing.smood.population.info.IndexInfoImpl;

/**
 * 
 */
public class IndexConfiguration {

	final Set<IndexInfo> indexInfos = newSet();
	final Map<String, Map<String, IndexInfo>> indexInfoMap = newMap();

	public void addMetricIndex(EntityType<?> et, String propertyName) {
		addIndex(et, propertyName, true);
	}

	public void addLookupIndex(EntityType<?> et, String propertyName) {
		addIndex(et, propertyName, false);
	}

	private void addIndex(EntityType<?> et, String propertyName, boolean metric) {
		IndexInfoImpl indexInfo = new IndexInfoImpl();

		indexInfo.setEntitySignature(et.getTypeSignature());
		indexInfo.setIndexId(indexId(et, propertyName));
		indexInfo.setPropertyName(propertyName);
		indexInfo.setHasMetric(metric);

		indexInfos.add(indexInfo);

		acquireMap(indexInfoMap, et.getTypeSignature()).put(propertyName, indexInfo);
	}

	public static String indexId(EntityType<?> et, String propertyName) {
		return et.getTypeSignature() + "#" + propertyName;
	}

}

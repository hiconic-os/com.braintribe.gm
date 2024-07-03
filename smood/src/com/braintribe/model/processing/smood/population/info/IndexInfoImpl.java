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
package com.braintribe.model.processing.smood.population.info;

import com.braintribe.model.processing.query.eval.api.repo.IndexInfo;

/**
 * 
 */
public class IndexInfoImpl implements IndexInfo {

	private String indexId;
	private boolean hasMetric;
	private String entitySignature;
	private String propertyName;

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	@Override
	public String getIndexId() {
		return indexId;
	}

	public void setHasMetric(boolean hasMetric) {
		this.hasMetric = hasMetric;
	}

	@Override
	public boolean hasMetric() {
		return hasMetric;
	}

	public void setEntitySignature(String entitySignature) {
		this.entitySignature = entitySignature;
	}

	@Override
	public String getEntitySignature() {
		return entitySignature;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

}

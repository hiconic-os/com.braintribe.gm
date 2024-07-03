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
package com.braintribe.model.processing.session.api.collaboration;

import com.braintribe.model.smoodstorage.stages.PersistenceStage;
import com.braintribe.model.smoodstorage.stages.StaticStage;

/**
 * @author peter.gazdik
 */
public abstract class SimplePersistenceInitializer extends AbstractPersistenceInitializer {

	protected StaticStage stage;

	@Override
	public PersistenceStage getPersistenceStage() {
		if (stage == null) {
			stage = StaticStage.T.create();
			stage.setName(stageName());
		}
		return stage;
	}

	protected String stageName() {
		return getClass().getName();
	}

	public static SimplePersistenceInitializer create(String stageName, DataInitializer dataInitializer) {
		return new SimplePersistenceInitializer() {
			// @formatter:off
			@Override protected String stageName() { return stageName; }
			@Override public void initializeData(PersistenceInitializationContext ctx) { dataInitializer.initialize(ctx); }
			// @formatter:on
		};
	}

}

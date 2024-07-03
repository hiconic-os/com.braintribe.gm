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
package com.braintribe.model.processing.mpc.evaluator.impl;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.processing.mpc.evaluator.api.MpcEvaluator;
import com.braintribe.model.processing.mpc.evaluator.api.MpcRegistry;

public class MpcRegistryImpl implements MpcRegistry {

	private Map<Class<? extends ModelPathCondition>, MpcEvaluator<?>> experts = new HashMap<Class<? extends ModelPathCondition>, MpcEvaluator<?>>();

	@Override
	public Map<Class<? extends ModelPathCondition>, MpcEvaluator<?>> getExperts() {
		return this.experts;
	}

	@Override
	public void setExperts(Map<Class<? extends ModelPathCondition>, MpcEvaluator<?>> experts) {
		this.experts = experts;
	}

	@Override
	public <D extends ModelPathCondition> void putExpert(Class<D> mpcType, MpcEvaluator<? super D> mpcEvaluator) {
		this.experts.put(mpcType, mpcEvaluator);
	}

	@Override
	public void removeExpert(Class<? extends ModelPathCondition> mpcType) {
		this.experts.remove(mpcType);
	}

	@Override
	public void resetRegistry() {
		this.experts = new HashMap<Class<? extends ModelPathCondition>, MpcEvaluator<?>>();
	}

	@Override
	public void loadOtherRegistry(MpcRegistry otherRegistry) {
		this.experts.putAll(otherRegistry.getExperts());
	}

}

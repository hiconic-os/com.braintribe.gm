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
package com.braintribe.model.processing.traversing.engine.impl.clone;

import com.braintribe.model.processing.traversing.api.GmTraversingVisitor;
import com.braintribe.model.processing.traversing.engine.api.ClonerConfigurer;
import com.braintribe.model.processing.traversing.engine.api.customize.ClonerCustomization;
import com.braintribe.model.processing.traversing.engine.impl.AbstractTraversingConfigurer;

public class ClonerConfigurerImpl extends AbstractTraversingConfigurer<ClonerConfigurerImpl> implements ClonerConfigurer<ClonerConfigurerImpl> {

	ClonerCustomization customizer = new BasicClonerCustomization();

	@Override
	public ClonerConfigurerImpl customize(ClonerCustomization customization) {
		this.customizer = customization;
		return this;
	}

	@Override
	public ClonerConfigurerImpl visitor(GmTraversingVisitor visitor) {
		if (visitor instanceof Cloner) {
			((Cloner) visitor).setCustomizer(customizer);
		}
		return super.visitor(visitor);
	}

}

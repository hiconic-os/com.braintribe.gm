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
package com.braintribe.model.processing.core.expert.impl;

import com.braintribe.cfg.Required;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.core.expert.api.GmExpertDefinition;
import com.braintribe.model.processing.core.expert.api.GmExpertSelector;
import com.braintribe.model.processing.core.expert.api.GmExpertSelectorContext;

public class ConfigurableGmExpertDefinition implements GmExpertSelector, GmExpertDefinition {

	private GenericModelType denotationType;
	private Class<?> expertType;
	private Object expert;
	private boolean assignable = true;
	
	@Required
	public void setDenotationType(Class<?> denotationType) {
		this.denotationType = GMF.getTypeReflection().getType(denotationType);
	}

	public void setExpertType(Class<?> expertType) {
		this.expertType = expertType;
	}

	public void setExpert(Object expert) {
		this.expert = expert;
	}
	
	@Override
	public GenericModelType denotationType() {
		return denotationType;
	}
	
	@Override
	public Class<?> expertType() {
		return expertType;
	}
	
	@Override
	public Object expert() {
		return expert;
	}
	
	@Override
	public boolean matches(GmExpertSelectorContext context) {
		return assignable? 
				denotationType.isAssignableFrom(context.getDenotationType()):
				denotationType == context.getDenotationType();
	}
}

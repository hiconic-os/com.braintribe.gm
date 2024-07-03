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
package com.braintribe.model.processing.aop.impl.context;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.aop.api.aspect.Advice;
import com.braintribe.model.processing.aop.api.context.BeforeContext;

public class BeforeContextImpl<I,O> extends AbstractContextImpl<I> implements BeforeContext<I,O>{
	
	private static Logger log = Logger.getLogger(BeforeContextImpl.class);
	
	private O response;
	private boolean skipped = false;
	private boolean overridden = false;
	
	@Override
	public void skip(O response) {
		this.response = response;
		skipped = true;
		log.info("Skip execution of joint point ["+ getJointPoint()+"], advice [" + getAdvice() + "]");
	}

	public O getResponse() {
		return response;
	}
	
	public boolean isSkipped() {
		return skipped;
	}
	
	@Override
	public void overrideRequest(I request) {
		overridden = true;
		setRequest( request);		
	}

	@Override
	public Advice getAdvice() {		
		return Advice.before;
	}
	
	public boolean isOverridden() {
		return overridden;
	}
	
	
	
	
	
}

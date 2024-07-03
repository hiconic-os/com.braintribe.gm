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
package com.braintribe.model.processing.session.impl.managed;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.session.api.managed.ManipulationApplicationContext;
import com.braintribe.model.processing.session.api.managed.ManipulationApplicationContextBuilder;
import com.braintribe.model.processing.session.api.managed.ManipulationLenience;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;
import com.braintribe.model.processing.session.api.managed.ManipulationReport;

/**
 * 
 */
public class BasicManipulationApplicationContext implements ManipulationApplicationContext, ManipulationApplicationContextBuilder {

	private final AbstractManagedGmSession session;
	private ManipulationMode mode;
	private ManipulationLenience lenience = ManipulationLenience.none;

	public BasicManipulationApplicationContext(AbstractManagedGmSession session) {
		this.session = session;
	}

	@Override
	public ManipulationReport apply(Manipulation manipulation) throws GmSessionException {
		return session.apply(manipulation, context());
	}

	@Override
	public ManipulationApplicationContext context() {
		return this;
	}

	@Override
	public ManipulationApplicationContextBuilder mode(ManipulationMode mode) {
		this.mode = mode;
		return this;
	}

	@Override
	public ManipulationMode getMode() {
		return mode;
	}
	
	@Override
	public ManipulationLenience getLenience() {
		return lenience;
	}
	
	@Override
	public ManipulationApplicationContextBuilder lenience(ManipulationLenience lenience) {
		this.lenience = lenience;
		return this;
	}
}

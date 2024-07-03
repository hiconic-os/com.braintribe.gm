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
package com.braintribe.model.access;

import com.braintribe.cfg.Required;

public abstract class AbstractDelegatingAccess extends AbstractDynamicDelegatingAccess {
	protected IncrementalAccess delegate;


	@Required
	public void setDelegate(IncrementalAccess delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * @see com.braintribe.model.access.AbstractDynamicDelegatingAccess#getDelegate()
	 */
	@Override
	protected IncrementalAccess getDelegate() {
		return delegate;
	}
	
}

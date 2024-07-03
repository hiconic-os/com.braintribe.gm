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
package com.braintribe.model.processing.rpc.commons.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.resource.CallStreamCapture;
import com.braintribe.model.resource.source.TransientSource;

public class RpcMarshallingStreamManagement {
	protected List<CallStreamCapture> callStreamCaptures = new ArrayList<>();
	protected List<TransientSource> transientSources = new ArrayList<TransientSource>();

	public RpcMarshallingStreamManagement() {
	}
	
	public Consumer<GenericEntity> getMarshallingVisitor() {
		return entity -> {
			// TODO: Optimize transient check when Peter finished his transient property reflection
			if (entity instanceof TransientSource) {
				transientSources.add((TransientSource) entity);
			}
			else if (entity instanceof CallStreamCapture) {
				callStreamCaptures.add((CallStreamCapture) entity);
			}
		};
	}
	
	public List<CallStreamCapture> getCallStreamCaptures() {
		return callStreamCaptures;
	}
	
	public List<TransientSource> getTransientSources() {
		return transientSources;
	}
	
}

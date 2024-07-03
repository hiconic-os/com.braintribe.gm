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
package com.braintribe.model.processing.session.api.managed;

import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.processing.async.api.AsyncCallback;

public interface MergeBuilder {

	MergeBuilder adoptUnexposed(boolean adopt);
	MergeBuilder keepEnvelope(boolean keepEvelope);
	MergeBuilder envelopeFactory(Function<GenericEntity, GenericEntity> envelopeFactory);
	MergeBuilder suspendHistory(boolean suspend);
	MergeBuilder transferTransientProperties(boolean transferTransientProperties);
	
	<T> T doFor(T data) throws GmSessionException;
	<T> void doFor(T data, AsyncCallback<T> asyncCallback) throws GmSessionException;

}

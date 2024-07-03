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
package com.braintribe.model.processing.session.api.persistence;

import com.braintribe.model.accessapi.ModelEnvironment;
import com.braintribe.processing.async.api.AsyncCallback;

public interface ModelEnvironmentDrivenGmSession extends PersistenceGmSession {
	public void configureModelEnvironment(ModelEnvironment modelEnvironment);
	public void configureModelEnvironment(ModelEnvironment modelEnvironment, AsyncCallback<Void> asyncCallback);
	public ModelEnvironment getModelEnvironment();
}

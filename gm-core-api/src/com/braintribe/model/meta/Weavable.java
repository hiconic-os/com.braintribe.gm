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
package com.braintribe.model.meta;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GmCoreApiInteropNamespaces;
import com.braintribe.model.generic.GmfException;
import com.braintribe.processing.async.api.AsyncCallback;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 * Marker interface for types which can be deployed (or woven using the Instant Type Weaving). For now we only support deployment of ProtoGmMetaModel.
 */
@JsType(namespace = GmCoreApiInteropNamespaces.metadata)
public interface Weavable {

	default void deploy() {
		try {
			GMF.getTypeReflection().deploy(this);

		} catch (GmfException e) {
			throw new RuntimeException("Error while deploying model!", e);
		}
	}

	@JsMethod(name = "deployAsync")
	default void deploy(AsyncCallback<Void> asyncCallack) {
		GMF.getTypeReflection().deploy(this, asyncCallack);
	}

}

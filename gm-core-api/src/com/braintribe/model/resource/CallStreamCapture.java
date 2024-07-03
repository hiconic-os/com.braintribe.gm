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
package com.braintribe.model.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.annotation.Transient;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.generic.session.OutputStreamProvider;
import com.braintribe.model.generic.session.exception.GmSessionRuntimeException;
import com.braintribe.model.resource.api.CallStreamCaptureSupport;

@ForwardDeclaration("com.braintribe.gm:transient-resource-model")
public interface CallStreamCapture extends GenericEntity {
	final EntityType<CallStreamCapture> T = EntityTypes.T(CallStreamCapture.class);
	
	@Transient
	OutputStreamProvider getOutputStreamProvider();
	void setOutputStreamProvider(OutputStreamProvider outputStreamProvider);

	default OutputStream openStream() {
		OutputStreamProvider outputStreamProvider = getOutputStreamProvider();
		
		if (outputStreamProvider != null) {
			try {
				return outputStreamProvider.openOutputStream();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		else {
			GmSession session = session();
	
			if (!(session instanceof CallStreamCaptureSupport)) {
				throw new GmSessionRuntimeException(
						"Cannot open capture stream as entity is not attached to a session which supports call stream capturing.");
			}
	
			CallStreamCaptureSupport callStreamCaptureSupport = (CallStreamCaptureSupport) session;
	
			try {
				return callStreamCaptureSupport.openStream(this);
	
			} catch (IOException e) {
				throw new RuntimeException("Error while opening capture stream:", e);
			}
		}
	}
	
	@Override
	default boolean hasTransientData() {
		return getOutputStreamProvider() != null;
	}
}

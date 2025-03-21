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
package dev.hiconic.servlet.webrpc.server.multipart;

import java.io.Closeable;
import java.util.function.Consumer;

import com.braintribe.codec.marshaller.api.MarshallerRegistryEntry;
import com.braintribe.model.processing.rpc.commons.impl.RpcUnmarshallingStreamManagement;
import com.braintribe.model.processing.service.api.ServiceRequestSummaryLogger;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.web.multipart.api.SequentialFormDataReader;

public interface MultipartRequestUnmarshaller extends Closeable {

	ServiceRequest unmarshall(ServiceRequestSummaryLogger summaryLogger, SequentialFormDataReader multiparts,
			RpcUnmarshallingStreamManagement streamManagement, Consumer<MarshallerRegistryEntry> marshallerEntryReceiver) throws Exception;

}

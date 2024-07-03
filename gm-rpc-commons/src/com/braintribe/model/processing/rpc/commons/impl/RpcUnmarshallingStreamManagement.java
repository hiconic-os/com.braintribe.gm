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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.braintribe.model.resource.source.TransientSource;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.stream.NullOutputStream;
import com.braintribe.utils.stream.api.StreamPipe;
import com.braintribe.utils.stream.api.StreamPipeFactory;

public class RpcUnmarshallingStreamManagement extends RpcMarshallingStreamManagement {
	private final Map<String, StreamPipe> pipes = new ConcurrentHashMap<>();
	private final String endpointShortDescription;
	private final StreamPipeFactory streamPipeFactory;

	private Map<String, TransientSource> sourcesCache = null;
//	/**
//	 * Please use the constructor with short description
//	 */
//	@Deprecated
//	public RpcUnmarshallingStreamManagement() {
//		endpointShortDescription = "gm-web-rpc";
//	}
	
	public RpcUnmarshallingStreamManagement(String endpointShortDescription, StreamPipeFactory streamPipeFactory) {
		this.endpointShortDescription = endpointShortDescription;
		this.streamPipeFactory = streamPipeFactory;
	}

	public void bindTransientSources() {
		for (TransientSource transientSource : transientSources) {
			String globalId = transientSource.getGlobalId();
			StreamPipe pipe = acquirePipe(globalId);
			transientSource.setInputStreamProvider(pipe::openInputStream);
		}
	}

	public void disarmCallStreamCaptures() {
		callStreamCaptures.forEach(c -> c.setOutputStreamProvider(NullOutputStream::getInstance));
	}

	public StreamPipe acquirePipe(String bindId) {
		return pipes.computeIfAbsent(bindId, id -> streamPipeFactory.newPipe(endpointShortDescription + "-stream-part-" + FileTools.replaceIllegalCharactersInFileName(id, "_")));
	}

	public void checkPipeSatisfaction() {
		List<String> unsatisfiedPipes = null;

		for (Map.Entry<String, StreamPipe> entry : pipes.entrySet()) {
			if (!entry.getValue().wasOutputStreamOpened()) {
				if (unsatisfiedPipes == null) {
					unsatisfiedPipes = new ArrayList<>();
				}
				unsatisfiedPipes.add(entry.getKey());
			}
		}

		if (unsatisfiedPipes != null) {
			throw new IllegalStateException("Missing data for instances of TransientSource with the following globalIds: " + unsatisfiedPipes);
		}
	}
	
	public TransientSource getTransientSourceWithId(String globalId) {
		if (sourcesCache == null)
			calculateTransientSourceCache();
		
		return sourcesCache.get(globalId);
	}

	private void calculateTransientSourceCache() {
		sourcesCache = transientSources.stream().collect(Collectors.toMap(t -> t.getGlobalId(), t -> t));
	}
}

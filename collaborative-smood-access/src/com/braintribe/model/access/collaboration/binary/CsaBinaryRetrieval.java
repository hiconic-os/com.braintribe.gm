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
package com.braintribe.model.access.collaboration.binary;

import java.io.File;
import java.util.Objects;
import java.util.function.Function;

import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.model.access.collaboration.CollaborativeSmoodAccess;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.FileSystemSource;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resourceapi.stream.BinaryRetrievalRequest;
import com.braintribe.model.resourceapi.stream.BinaryRetrievalResponse;

/**
 * A special {@link ServiceProcessor} for {@link BinaryRetrievalRequest}s of {@link FileSystemSource}(s).
 */
public class CsaBinaryRetrieval implements ServiceProcessor<BinaryRetrievalRequest, BinaryRetrievalResponse> {

	private ServiceProcessor<BinaryRetrievalRequest, BinaryRetrievalResponse> retrievalDelegate;
	private Function<String, File> accessIdToResourcesBase;
	private Function<String, CollaborativeSmoodAccess> csaAccessResolver;

	@Required
	public void setRetrievalDelegate(ServiceProcessor<? super BinaryRetrievalRequest, ? super BinaryRetrievalResponse> retrievalDelegate) {
		this.retrievalDelegate = (ServiceProcessor<BinaryRetrievalRequest, BinaryRetrievalResponse>) retrievalDelegate;
	}

	/** Configures a function which for given <tt>accessId</tt> returns the (absolute) resources base folder. */
	@Required
	public void setAccessIdToResourcesBase(Function<String, File> accessIdToResourcesBase) {
		this.accessIdToResourcesBase = accessIdToResourcesBase;
	}

	@Required
	public void setDeployRegistry(Function<String, CollaborativeSmoodAccess> csaAccessResolver) {
		this.csaAccessResolver = csaAccessResolver;
	}

	@Override
	public BinaryRetrievalResponse process(ServiceRequestContext context, BinaryRetrievalRequest request) {
		Resource resource = request.getResource();
		FileSystemSource source = retrieveFileSystemSource(resource);
		if (source != null)
			lazyLoadFileSystemSourceIfNeeded(request.getDomainId(), source);

		return retrievalDelegate.process(context, request);
	}

	private static FileSystemSource retrieveFileSystemSource(Resource resource) {
		Objects.requireNonNull(resource, "Cannot stream null Resource");

		ResourceSource source = resource.getResourceSource();
		Objects.requireNonNull(source, () -> "Cannot stream resource with null source: " + resource);

		if (source instanceof FileSystemSource)
			return (FileSystemSource) source;
		else
			return null;
	}

	private void lazyLoadFileSystemSourceIfNeeded(String accessId, FileSystemSource source) {
		File sourceFile = resolveSourcePath(accessId, source);
		if (sourceFile.exists())
			return;

		CollaborativeSmoodAccess csa = resolveCsa(accessId, source);

		if (!csa.experimentalLazyLoad(source))
			throw new IllegalStateException(
					"Resource data not found (locally nor via lazy-loading). Access:" + accessId + ", path: " + source.getPath());
	}

	private CollaborativeSmoodAccess resolveCsa(String accessId, FileSystemSource source) {
		try {
			return csaAccessResolver.apply(accessId);

		} catch (RuntimeException e) {
			throw Exceptions.contextualize(e,
					"Resolution of access '" + accessId + "' failed, thus won't be able to lazy-load missing source: " + source.getPath());
		}
	}

	private File resolveSourcePath(String accessId, FileSystemSource source) {
		File absoluteResourcesBase = accessIdToResourcesBase.apply(accessId);
		String relativePath = source.getPath();
		return new File(absoluteResourcesBase, relativePath);
	}

}

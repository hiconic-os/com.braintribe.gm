// ============================================================================
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
package com.braintribe.testing.tools.gm.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.generic.session.OutputStreamProvider;
import com.braintribe.model.generic.session.OutputStreamer;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.api.resource.ResourceAccess;
import com.braintribe.model.processing.session.api.resource.ResourceCreateBuilder;
import com.braintribe.model.processing.session.api.resource.ResourceDeleteBuilder;
import com.braintribe.model.processing.session.api.resource.ResourceRetrieveBuilder;
import com.braintribe.model.processing.session.api.resource.ResourceUpdateBuilder;
import com.braintribe.model.processing.session.api.resource.ResourceUrlBuilder;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.BlobSource;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resource.specification.ResourceSpecification;
import com.braintribe.model.resourceapi.persistence.DeletionScope;
import com.braintribe.model.resourceapi.stream.BinaryRetrievalResponse;
import com.braintribe.model.resourceapi.stream.condition.StreamCondition;
import com.braintribe.model.resourceapi.stream.range.StreamRange;
import com.braintribe.utils.StringTools;

/**
 * A {@link ResourceAccess} for tests, which keeps the binary data of every {@link Resource} in memory.
 * <p>
 * It follows the same principle as a real access: a {@link Resource} points to a {@link BlobSource}, and the data is kept outside of the entity, here
 * in a map that is keyed by the id of the source.
 * <p>
 * Create an instance with an {@link InMemoryResourceAccessFactory}, so that every session on the same access sees the same data.
 *
 * @see InMemoryResourceAccessFactory
 */
public class InMemoryResourceAccess implements ResourceAccess {

	private final PersistenceGmSession session;
	private final Map<String, byte[]> dataBySourceId;

	public InMemoryResourceAccess(PersistenceGmSession session, Map<String, byte[]> dataBySourceId) {
		this.session = session;
		this.dataBySourceId = dataBySourceId;
	}

	// ###############################################
	// ## . . . . . . . . . Create . . . . . . . . .##
	// ###############################################

	@Override
	public ResourceCreateBuilder create() {
		return new CreateBuilder();
	}

	@Override
	public ResourceUpdateBuilder update(Resource resource) {
		return new UpdateBuilder(resource);
	}

	private class CreateBuilder implements ResourceCreateBuilder {

		protected String name;
		protected String mimeType;
		protected String md5;
		protected String useCase;
		protected String creator;
		protected Set<String> tags;
		protected ResourceSpecification specification;

		@Override
		public ResourceCreateBuilder mimeType(String mimeType) {
			this.mimeType = mimeType;
			return this;
		}

		@Override
		public ResourceCreateBuilder md5(String md5) {
			this.md5 = md5;
			return this;
		}

		@Override
		public ResourceCreateBuilder useCase(String useCase) {
			this.useCase = useCase;
			return this;
		}

		@Override
		public ResourceCreateBuilder tags(Set<String> tags) {
			this.tags = tags;
			return this;
		}

		@Override
		public ResourceCreateBuilder sourceType(EntityType<? extends ResourceSource> sourceType) {
			if (sourceType != null && sourceType != BlobSource.T)
				throw new UnsupportedOperationException(
						InMemoryResourceAccess.class.getSimpleName() + " only supports " + BlobSource.T.getShortName() + ", not: " + sourceType);

			return this;
		}

		@Override
		public ResourceCreateBuilder specification(ResourceSpecification specification) {
			this.specification = specification;
			return this;
		}

		@Override
		public ResourceCreateBuilder name(String resourceName) {
			this.name = resourceName;
			return this;
		}

		@Override
		public ResourceCreateBuilder creator(String creator) {
			this.creator = creator;
			return this;
		}

		@Override
		public Resource store(InputStream inputStream) {
			return store(readAllBytes(inputStream));
		}

		@Override
		public Resource store(InputStreamProvider inputStreamProvider) {
			try (InputStream in = inputStreamProvider.openInputStream()) {
				return store(readAllBytes(in));

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public Resource store(OutputStreamer streamer) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				streamer.writeTo(out);

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}

			return store(out.toByteArray());
		}

		/** Creates the Resource with its BlobSource, and keeps the data under the id of that source. */
		protected Resource store(byte[] data) {
			return fill(session.create(Resource.T), data);
		}

		protected Resource fill(Resource resource, byte[] data) {
			BlobSource source = session.create(BlobSource.T);
			source.setId(UUID.randomUUID().toString());
			source.setUseCase(useCase);

			dataBySourceId.put(source.getId(), data);

			if (resource.getId() == null)
				resource.setId(source.getId());

			resource.setName(name);
			resource.setMimeType(mimeType != null ? mimeType : guessMimeType(name));
			resource.setMd5(md5 != null ? md5 : md5Of(data));
			resource.setFileSize((long) data.length);
			resource.setCreated(new Date());
			resource.setCreator(creator);
			resource.setSpecification(specification);
			resource.setResourceSource(source);

			if (tags != null)
				resource.getTags().addAll(tags);

			return resource;
		}
	}

	private class UpdateBuilder extends CreateBuilder implements ResourceUpdateBuilder {

		private final Resource resource;
		private boolean deleteOldResourceSource = true;

		public UpdateBuilder(Resource resource) {
			this.resource = resource;
			this.name = resource.getName();
			this.mimeType = resource.getMimeType();
		}

		@Override
		public ResourceUpdateBuilder deleteOldResourceSource(boolean deleteOldResourceSource) {
			this.deleteOldResourceSource = deleteOldResourceSource;
			return this;
		}

		@Override
		protected Resource store(byte[] data) {
			ResourceSource oldSource = resource.getResourceSource();

			Resource result = fill(resource, data);

			if (oldSource != null && deleteOldResourceSource) {
				dataBySourceId.remove(oldSource.getId());
				session.deleteEntity(oldSource);
			}

			return result;
		}

		// @formatter:off
		@Override public ResourceUpdateBuilder mimeType(String mimeType) { super.mimeType(mimeType); return this; }
		@Override public ResourceUpdateBuilder md5(String md5) { super.md5(md5); return this; }
		@Override public ResourceUpdateBuilder useCase(String useCase) { super.useCase(useCase); return this; }
		@Override public ResourceUpdateBuilder tags(Set<String> tags) { super.tags(tags); return this; }
		@Override public ResourceUpdateBuilder specification(ResourceSpecification s) { super.specification(s); return this; }
		@Override public ResourceUpdateBuilder name(String resourceName) { super.name(resourceName); return this; }
		@Override public ResourceUpdateBuilder creator(String creator) { super.creator(creator); return this; }
		@Override public ResourceUpdateBuilder sourceType(EntityType<? extends ResourceSource> sourceType) { super.sourceType(sourceType); return this; }
		// @formatter:on
	}

	// ###############################################
	// ## . . . . . . . . Retrieve . . . . . . . . .##
	// ###############################################

	@Override
	public ResourceRetrieveBuilder retrieve(Resource resource) {
		return new RetrieveBuilder(resource);
	}

	private class RetrieveBuilder implements ResourceRetrieveBuilder {

		private final Resource resource;
		private StreamRange range;

		public RetrieveBuilder(Resource resource) {
			this.resource = resource;
		}

		@Override
		public ResourceRetrieveBuilder range(StreamRange range) {
			this.range = range;
			return this;
		}

		@Override
		public ResourceRetrieveBuilder condition(StreamCondition condition) {
			throw new UnsupportedOperationException(
					InMemoryResourceAccess.class.getSimpleName() + " does not support a StreamCondition. Condition: " + condition);
		}

		@Override
		public ResourceRetrieveBuilder onResponse(Consumer<BinaryRetrievalResponse> consumer) {
			throw new UnsupportedOperationException(InMemoryResourceAccess.class.getSimpleName() + " does not support a response consumer.");
		}

		@Override
		public InputStream stream() {
			return new ByteArrayInputStream(rangedData());
		}

		@Override
		public void stream(OutputStream outputStream) {
			try {
				outputStream.write(rangedData());

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public void stream(OutputStreamProvider outputStreamProvider) {
			try (OutputStream out = outputStreamProvider.openOutputStream()) {
				stream(out);

			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		private byte[] rangedData() {
			byte[] data = dataOf(resource);
			if (range == null)
				return data;

			int start = range.getStart() == null ? 0 : range.getStart().intValue();
			int end = range.getEnd() == null ? data.length - 1 : range.getEnd().intValue();

			byte[] result = new byte[Math.max(0, end - start + 1)];
			System.arraycopy(data, start, result, 0, result.length);

			return result;
		}
	}

	/** The data of given Resource. Useful for an assertion in a test. */
	public byte[] dataOf(Resource resource) {
		ResourceSource source = resource.getResourceSource();
		if (source == null)
			throw new IllegalStateException("Resource has no source: " + resource);

		byte[] result = dataBySourceId.get(source.getId());
		if (result == null)
			throw new IllegalStateException("There is no data for the source of this Resource: " + resource + ", source id: " + source.getId());

		return result;
	}

	// ###############################################
	// ## . . . . . . . . . Delete . . . . . . . . .##
	// ###############################################

	@Override
	public ResourceDeleteBuilder delete(Resource resource) {
		return new DeleteBuilder(resource);
	}

	private class DeleteBuilder implements ResourceDeleteBuilder {

		private final Resource resource;
		private DeletionScope scope = DeletionScope.resource;

		public DeleteBuilder(Resource resource) {
			this.resource = resource;
		}

		@Override
		@Deprecated
		public ResourceDeleteBuilder useCase(String useCase) {
			return this;
		}

		@Override
		public ResourceDeleteBuilder scope(DeletionScope scope) {
			this.scope = scope;
			return this;
		}

		@Override
		public void delete() {
			ResourceSource source = resource.getResourceSource();
			if (source != null)
				dataBySourceId.remove(source.getId());

			switch (scope) {
				case resource:
					session.deleteEntity(resource);
					deleteSourceEntity(source);
					break;
				case source:
					deleteSourceEntity(source);
					break;
				case binary:
					break;
				default:
					throw new IllegalStateException("Unknown deletion scope: " + scope);
			}
		}

		private void deleteSourceEntity(ResourceSource source) {
			if (source != null)
				session.deleteEntity(source);
		}
	}

	// ###############################################
	// ## . . . . . . . . . . Url . . . . . . . . . ##
	// ###############################################

	@Override
	public ResourceUrlBuilder url(Resource resource) {
		return new UrlBuilder(resource);
	}

	private static class UrlBuilder implements ResourceUrlBuilder {

		private final Resource resource;

		public UrlBuilder(Resource resource) {
			this.resource = resource;
		}

		@Override
		public String asString() {
			ResourceSource source = resource.getResourceSource();

			return "in-memory:" + (source == null ? "unknown" : source.getId());
		}

		// @formatter:off
		@Override public ResourceUrlBuilder download(boolean download) { return this; }
		@Override public ResourceUrlBuilder fileName(String fileName) { return this; }
		@Override public ResourceUrlBuilder accessId(String accessId) { return this; }
		@Override public ResourceUrlBuilder sessionId(String sessionId) { return this; }
		@Override public ResourceUrlBuilder sourceType(String sourceTypeSignature) { return this; }
		@Override public ResourceUrlBuilder useCase(String useCase) { return this; }
		@Override public ResourceUrlBuilder mimeType(String mimeType) { return this; }
		@Override public ResourceUrlBuilder md5(String md5) { return this; }
		@Override public ResourceUrlBuilder creator(String creator) { return this; }
		@Override public ResourceUrlBuilder tags(String tags) { return this; }
		@Override public ResourceUrlBuilder specification(String specification) { return this; }
		@Override public ResourceUrlBuilder base(String baseUrl) { return this; }
		// @formatter:on
	}

	// ###############################################
	// ## . . . . . . . . . Commons . . . . . . . . ##
	// ###############################################

	private static byte[] readAllBytes(InputStream in) {
		try {
			return in.readAllBytes();

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static String md5Of(byte[] data) {
		try {
			return StringTools.toHex(MessageDigest.getInstance("MD5").digest(data));

		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Error while computing the MD5 of the Resource data.");
		}
	}

	private static String guessMimeType(String name) {
		return name == null ? null : URLConnection.guessContentTypeFromName(name);
	}

}

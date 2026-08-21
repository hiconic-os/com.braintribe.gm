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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.source.BlobSource;
import com.braintribe.model.resource.source.FileSystemSource;
import com.braintribe.model.resource.source.ResourceSource;
import com.braintribe.model.resourceapi.persistence.DeletionScope;
import com.braintribe.model.resourceapi.stream.range.StreamRange;
import com.braintribe.model.util.meta.NewMetaModelGeneration;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * Tests for {@link InMemoryResourceAccess} and {@link InMemoryResourceAccessFactory}.
 */
public class InMemoryResourceAccessTest {

	private static final String CONTENT = "Hello, Resource!";
	private static final byte[] CONTENT_BYTES = CONTENT.getBytes(StandardCharsets.UTF_8);

	private SmoodAccess access;
	private InMemoryResourceAccessFactory resourceAccessFactory;

	@Before
	public void setup() {
		GmMetaModel model = new NewMetaModelGeneration() //
				.buildMetaModel("test:InMemoryResourceAccessTestModel", asList(Resource.T, BlobSource.T));

		access = GmTestTools.newSmoodAccessMemoryOnly("test.access", model);
		resourceAccessFactory = GmTestTools.newInMemoryResourceAccessFactory();
	}

	private PersistenceGmSession newSession() {
		return GmTestTools.newSessionWithInMemoryResources(access, resourceAccessFactory);
	}

	// ###############################################
	// ## . . . . . . . . . Create . . . . . . . . .##
	// ###############################################

	@Test
	public void createFillsResourceAndStoresData() {
		PersistenceGmSession session = newSession();

		Resource resource = session.resources().create() //
				.name("hello.txt") //
				.store(newContentStream());

		assertThat(resource.getName()).isEqualTo("hello.txt");
		assertThat(resource.getFileSize()).isEqualTo(CONTENT_BYTES.length);
		assertThat(resource.getCreated()).isNotNull();
		assertThat(resource.getMd5()).matches("(?i)[0-9a-f]{32}");
		assertThat(resource.getMimeType()).isEqualTo("text/plain");

		ResourceSource source = resource.getResourceSource();
		assertThat(source).isInstanceOf(BlobSource.class);
		assertThat((String) source.getId()).isNotNull();
		assertThat(resourceAccessFactory.dataOf(resource)).isEqualTo(CONTENT_BYTES);
	}

	@Test
	public void createFromInputStreamProviderStoresData() {
		PersistenceGmSession session = newSession();

		Resource resource = session.resources().create() //
				.name("hello.txt") //
				.store(this::newContentStream);

		assertThat(resourceAccessFactory.dataOf(resource)).isEqualTo(CONTENT_BYTES);
	}

	@Test
	public void createFromOutputStreamerStoresData() {
		PersistenceGmSession session = newSession();

		Resource resource = session.resources().create() //
				.name("hello.txt") //
				.store(out -> out.write(CONTENT_BYTES));

		assertThat(resourceAccessFactory.dataOf(resource)).isEqualTo(CONTENT_BYTES);
	}

	@Test
	public void explicitValuesAreKept() {
		PersistenceGmSession session = newSession();

		Resource resource = session.resources().create() //
				.name("hello.txt") //
				.mimeType("application/custom") //
				.md5("0123456789abcdef0123456789abcdef") //
				.creator("tester") //
				.useCase("my-use-case") //
				.tags(Set.of("tag-one", "tag-two")) //
				.store(newContentStream());

		assertThat(resource.getMimeType()).isEqualTo("application/custom");
		assertThat(resource.getMd5()).isEqualTo("0123456789abcdef0123456789abcdef");
		assertThat(resource.getCreator()).isEqualTo("tester");
		assertThat(resource.getTags()).containsExactlyInAnyOrder("tag-one", "tag-two");
		assertThat(resource.getResourceSource().getUseCase()).isEqualTo("my-use-case");
	}

	@Test
	public void mimeTypeStaysNullWhenNameHasNoKnownExtension() {
		PersistenceGmSession session = newSession();

		Resource resource = session.resources().create() //
				.name("hello") //
				.store(newContentStream());

		assertThat(resource.getMimeType()).isNull();
	}

	@Test
	public void identicalContentGivesSameMd5_differentContentDoesNot() {
		PersistenceGmSession session = newSession();

		Resource first = session.resources().create().name("a.txt").store(newContentStream());
		Resource second = session.resources().create().name("b.txt").store(newContentStream());
		Resource other = session.resources().create().name("c.txt").store(new ByteArrayInputStream("other".getBytes(StandardCharsets.UTF_8)));

		assertThat(second.getMd5()).isEqualTo(first.getMd5());
		assertThat(other.getMd5()).isNotEqualTo(first.getMd5());
	}

	@Test
	public void everyResourceGetsItsOwnSource() {
		PersistenceGmSession session = newSession();

		Resource first = session.resources().create().name("a.txt").store(newContentStream());
		Resource second = session.resources().create().name("b.txt").store(newContentStream());

		assertThat((String) first.getResourceSource().getId()).isNotEqualTo(second.getResourceSource().getId());
		assertThat(resourceAccessFactory.storedBinaryCount()).isEqualTo(2);
	}

	@Test
	public void unsupportedSourceTypeFails() {
		PersistenceGmSession session = newSession();

		assertThatThrownBy(() -> session.resources().create().sourceType(FileSystemSource.T)) //
				.isInstanceOf(UnsupportedOperationException.class) //
				.hasMessageContaining("BlobSource");
	}

	// ###############################################
	// ## . . . . . . . . Retrieve . . . . . . . . .##
	// ###############################################

	@Test
	public void retrieveReturnsStoredData() throws Exception {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		try (InputStream in = session.resources().retrieve(resource).stream()) {
			assertThat(in.readAllBytes()).isEqualTo(CONTENT_BYTES);
		}
	}

	@Test
	public void openStreamReturnsStoredData() throws Exception {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		try (InputStream in = session.resources().openStream(resource)) {
			assertThat(in.readAllBytes()).isEqualTo(CONTENT_BYTES);
		}
	}

	@Test
	public void retrieveWritesToOutputStream() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		session.resources().retrieve(resource).stream(out);

		assertThat(out.toByteArray()).isEqualTo(CONTENT_BYTES);
	}

	@Test
	public void retrieveHonoursRange() throws Exception {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		// "Hello, Resource!" -> characters 7 to 14 are "Resource"
		try (InputStream in = session.resources().retrieve(resource).range(StreamRange.create(7L, 14L)).stream()) {
			assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("Resource");
		}
	}

	@Test
	public void streamConditionIsNotSupported() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		assertThatThrownBy(() -> session.resources().retrieve(resource).condition(null)) //
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void retrievingResourceWithoutDataFails() {
		PersistenceGmSession session = newSession();
		Resource resource = session.create(Resource.T);
		resource.setName("never-stored.txt");

		assertThatThrownBy(() -> session.resources().retrieve(resource).stream()) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining("no source");
	}

	// ###############################################
	// ## . . . . . One access, many sessions . . . .##
	// ###############################################

	@Test
	public void anotherSessionOnSameAccessSeesData() throws Exception {
		PersistenceGmSession writeSession = newSession();
		writeSession.resources().create().name("hello.txt").store(newContentStream());
		writeSession.commit();

		PersistenceGmSession readSession = newSession();
		Resource loaded = readSession.query().entities(EntityQuery.create(Resource.T)).unique();

		assertThat(loaded).isNotNull();
		try (InputStream in = readSession.resources().retrieve(loaded).stream()) {
			assertThat(in.readAllBytes()).isEqualTo(CONTENT_BYTES);
		}
	}

	// ###############################################
	// ## . . . . . . . . . Update . . . . . . . . .##
	// ###############################################

	@Test
	public void updateReplacesDataAndDropsOldSource() throws Exception {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());
		session.commit();

		String oldSourceId = resource.getResourceSource().getId();

		session.resources().update(resource).store(new ByteArrayInputStream("updated".getBytes(StandardCharsets.UTF_8)));
		session.commit();

		assertThat((String) resource.getResourceSource().getId()).isNotEqualTo(oldSourceId);
		assertThat(resourceAccessFactory.dataOfSource(oldSourceId)).isNull();
		assertThat(resourceAccessFactory.storedBinaryCount()).isEqualTo(1);

		try (InputStream in = session.resources().retrieve(resource).stream()) {
			assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("updated");
		}

		assertThat(allSources(session)).hasSize(1);
	}

	@Test
	public void updateKeepsOldSourceWhenAsked() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());
		session.commit();

		String oldSourceId = resource.getResourceSource().getId();

		session.resources().update(resource) //
				.deleteOldResourceSource(false) //
				.store(new ByteArrayInputStream("updated".getBytes(StandardCharsets.UTF_8)));
		session.commit();

		assertThat(resourceAccessFactory.dataOfSource(oldSourceId)).isEqualTo(CONTENT_BYTES);
		assertThat(resourceAccessFactory.storedBinaryCount()).isEqualTo(2);
		assertThat(allSources(session)).hasSize(2);
	}

	@Test
	public void updateKeepsNameWhenItIsNotChanged() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		session.resources().update(resource).store(new ByteArrayInputStream("updated".getBytes(StandardCharsets.UTF_8)));

		assertThat(resource.getName()).isEqualTo("hello.txt");
	}

	// ###############################################
	// ## . . . . . . . . . Delete . . . . . . . . .##
	// ###############################################

	@Test
	public void deleteRemovesDataAndBothEntities() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());
		session.commit();

		session.resources().delete(resource).delete();
		session.commit();

		assertThat(resourceAccessFactory.storedBinaryCount()).isZero();
		assertThat(allResources(session)).isEmpty();
		assertThat(allSources(session)).isEmpty();
	}

	@Test
	public void deleteWithSourceScopeKeepsResourceEntity() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());
		session.commit();

		session.resources().delete(resource).scope(DeletionScope.source).delete();
		session.commit();

		assertThat(resourceAccessFactory.storedBinaryCount()).isZero();
		assertThat(allResources(session)).hasSize(1);
		assertThat(allSources(session)).isEmpty();
	}

	@Test
	public void deleteWithBinaryScopeKeepsBothEntities() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());
		session.commit();

		session.resources().delete(resource).scope(DeletionScope.binary).delete();
		session.commit();

		assertThat(resourceAccessFactory.storedBinaryCount()).isZero();
		assertThat(allResources(session)).hasSize(1);
		assertThat(allSources(session)).hasSize(1);
	}

	// ###############################################
	// ## . . . . . . . . Factory . . . . . . . . . ##
	// ###############################################

	@Test
	public void separateFactoriesDoNotShareData() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		InMemoryResourceAccessFactory otherFactory = GmTestTools.newInMemoryResourceAccessFactory();

		assertThat(otherFactory.dataOf(resource)).isNull();
		assertThat(resourceAccessFactory.dataOf(resource)).isEqualTo(CONTENT_BYTES);
	}

	@Test
	public void clearForgetsData() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		resourceAccessFactory.clear();

		assertThat(resourceAccessFactory.storedBinaryCount()).isZero();
		assertThat(resourceAccessFactory.dataOf(resource)).isNull();
	}

	@Test
	public void urlMentionsSourceId() {
		PersistenceGmSession session = newSession();
		Resource resource = session.resources().create().name("hello.txt").store(newContentStream());

		String resourceUrl = session.resources().url(resource).asString();
		String resourceId = resource.getResourceSource().getId();
		assertThat(resourceUrl).contains(resourceId);
	}

	// ###############################################
	// ## . . . . . . . . Helpers . . . . . . . . . ##
	// ###############################################

	private InputStream newContentStream() {
		return new ByteArrayInputStream(CONTENT_BYTES);
	}

	private static List<Resource> allResources(PersistenceGmSession session) {
		return session.query().entities(EntityQuery.create(Resource.T)).list();
	}

	private static List<ResourceSource> allSources(PersistenceGmSession session) {
		return session.query().entities(EntityQuery.create(ResourceSource.T)).list();
	}

}

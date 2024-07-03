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
package com.braintribe.gm.marshaller.resource.threshold.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.gm.marshaller.threshold.OnThresholdResourceCreated;
import com.braintribe.gm.marshaller.threshold.ThresholdPersistenceMarshaller;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resourceapi.persistence.UploadResource;
import com.braintribe.model.resourceapi.persistence.UploadResourceResponse;
import com.braintribe.model.resourceapi.stream.GetBinaryResponse;
import com.braintribe.model.resourceapi.stream.GetResource;
import com.braintribe.model.user.User;
import com.braintribe.provider.Holder;
import com.braintribe.utils.stream.api.StreamPipe;
import com.braintribe.utils.stream.api.StreamPipes;

public class ThresholdPersistenceMarshallerTest extends ResourceAwareMarshallerTestBase {
	public static final String ACCESS_ID_MARSHALLING = "access.marshalling.test";

	@Test
	public void testResourceRoundtrip() {
		byte[] data = new byte[64*1024];
		new Random().nextBytes(data);
		
		Resource resource = Resource.createTransient(() -> new ByteArrayInputStream(data));
		
		UploadResource uploadResource = UploadResource.T.create();
		uploadResource.setDomainId(ACCESS_ID_MARSHALLING);
		uploadResource.setResource(resource);
		
		UploadResourceResponse uploadResourceResponse = uploadResource.eval(evaluator).get();
		
		Resource persistedResource = uploadResourceResponse.getResource();
		
		GetResource getResource = GetResource.T.create();
		getResource.setDomainId(ACCESS_ID_MARSHALLING);
		getResource.setResource(persistedResource);
		
		GetBinaryResponse getBinaryResponse = getResource.eval(evaluator).get();
		
		Resource trippedResource = getBinaryResponse.getResource();
		
		assertThat(trippedResource.openStream()).hasSameContentAs(new ByteArrayInputStream(data));
	}
	
	@Test
	public void testUnderThreshold() throws IOException {
		ThresholdPersistenceMarshaller marshaller = testContract.thresholdPersistenceMarshaller();
		
		int size = 20;
		
		List<User> users = createUsers(size);
		
		StreamPipe pipe = StreamPipes.simpleFactory().newPipe("test");
		
		Holder<Resource> persistedResourceHolder = new Holder<>();
		
		try (OutputStream out = pipe.acquireOutputStream()) {
			marshaller.marshall(out, users, GmSerializationOptions.defaultOptions.derive().set(OnThresholdResourceCreated.class, 
					(s,r) -> persistedResourceHolder.accept(r)).build());
		}
		
		assertThat(persistedResourceHolder.get()).isNull();
		
		List<User> actualUsers;
		
		try (InputStream in = pipe.openInputStream()) {
			actualUsers = (List<User>) marshaller.unmarshall(in);
		}
		
		validateData(size, users, actualUsers);
	}
	
	@Test
	public void testAboveThreshold() throws IOException {
		ThresholdPersistenceMarshaller marshaller = testContract.thresholdPersistenceMarshaller();
		
		int size = 10000;
		
		List<User> users = createUsers(size);
		
		StreamPipe pipe = StreamPipes.simpleFactory().newPipe("test");
		
		Holder<Resource> persistedResourceHolder = new Holder<>();
		
		try (OutputStream out = pipe.acquireOutputStream()) {
			marshaller.marshall(out, users, GmSerializationOptions.defaultOptions.derive().set(OnThresholdResourceCreated.class, 
					(s,r) -> persistedResourceHolder.accept(r)).build());
		}
		
		assertThat(persistedResourceHolder.get()).isNotNull();
		
		List<User> actualUsers;
		
		try (InputStream in = pipe.openInputStream()) {
			actualUsers = (List<User>) marshaller.unmarshall(in);
		}
		
		validateData(size, users, actualUsers);
	}

	private void validateData(int size, List<User> users, List<User> actualUsers) {
		assertThat(actualUsers.size()).isEqualTo(users.size());
		
		for (int i = 0; i < size; i++) {
			User user = users.get(i);
			User actualUser = actualUsers.get(i);
			assertThat(actualUser.getName()).isEqualTo(user.getName());
		}
	}

	private List<User> createUsers(int size) {
		List<User> users = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			users.add(createUser());
		}
		return users;
	}

	private User createUser() {
		User user = User.T.create();
		user.setName(UUID.randomUUID().toString());
		return user;
	}
}

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
package com.braintribe.transport.http;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.braintribe.utils.stream.MemoryThresholdBuffer;

public class MemoryThresholdBufferEntityTest {

	@Test
	public void testBuffer() throws Exception {
		
		byte[] bytes = "Hello, World!".getBytes("UTF-8");
		
		MemoryThresholdBuffer buffer = new MemoryThresholdBuffer();
		buffer.write(bytes);
		buffer.close();
		
		MemoryThresholdBufferEntity entity = new MemoryThresholdBufferEntity(buffer);
		
		assertThat(entity.isRepeatable()).isTrue();
		assertThat(entity.isStreaming()).isTrue();
		assertThat(entity.getContentLength()).isEqualTo(bytes.length);
		assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream(bytes));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		entity.writeTo(baos);
		assertThat(new ByteArrayInputStream(baos.toByteArray())).hasSameContentAs(new ByteArrayInputStream(bytes));
	}
	
	@Test
	public void testBufferThresholdExceeds() throws Exception {
		
		byte[] bytes = "Hello, World!".getBytes("UTF-8");
		
		MemoryThresholdBuffer buffer = new MemoryThresholdBuffer(5);
		buffer.write(bytes);
		buffer.close();
		
		MemoryThresholdBufferEntity entity = new MemoryThresholdBufferEntity(buffer);
		
		assertThat(entity.isRepeatable()).isFalse();
		assertThat(entity.isStreaming()).isTrue();
		assertThat(entity.getContentLength()).isEqualTo(bytes.length);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		entity.writeTo(baos);
		assertThat(new ByteArrayInputStream(baos.toByteArray())).hasSameContentAs(new ByteArrayInputStream(bytes));
	}
}

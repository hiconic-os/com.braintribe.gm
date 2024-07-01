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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.resource.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MimeTypeRegistryImplTest {

	@Test
	public void testRegistry() {

		MimeTypeRegistryImpl registry = new MimeTypeRegistryImpl();

		registry.registerMapping("audio/mp4", "m4a");
		registry.registerMapping("audio/mp4", "mp4a");

		assertThat(registry.getExtensions("audio/mp4")).hasSize(2);
		assertThat(registry.getExtensions("audio/mp4")).contains("m4a", "mp4a");
		assertThat(registry.getMimeTypes("m4a")).hasSize(1);
		assertThat(registry.getMimeTypes("m4a")).contains("audio/mp4");

		registry.registerMapping("video/x-mpeg", "mp3");
		registry.registerMapping("audio/mpeg", "mp3");
		registry.registerMapping("audio/mpeg3", "mp3");

		assertThat(registry.getMimeTypes("mp3")).hasSize(3);
		assertThat(registry.getMimeTypes("mp3")).contains("video/x-mpeg", "audio/mpeg", "audio/mpeg3");

	}

}

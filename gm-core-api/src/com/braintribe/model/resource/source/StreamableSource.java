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
package com.braintribe.model.resource.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import com.braintribe.model.generic.annotation.GmSystemInterface;
import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.generic.session.OutputStreamer;
import com.braintribe.model.resource.Resource;

/**
 * A unified interface for {@link Resource} and (some) {@link ResourceSource}s that allows to access the its {@link InputStream} and
 * {@link OutputStream}.
 */
@GmSystemInterface
public interface StreamableSource {

	InputStreamProvider inputStreamProvider();

	default InputStream openStream() {
		InputStreamProvider inputStreamProvider = inputStreamProvider();

		if (inputStreamProvider == null)
			throw new IllegalStateException("No InputStreamProvider defined for: " + this);

		try {
			return inputStreamProvider.openInputStream();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	default void writeToStream(OutputStream outputStream) {
		InputStreamProvider inputStreamProvider = inputStreamProvider();

		if (inputStreamProvider == null)
			throw new IllegalStateException("No InputStreamProvider defined for: " + this);

		try {
			if (inputStreamProvider instanceof OutputStreamer) {
				((OutputStreamer) inputStreamProvider).writeTo(outputStream);
			} else {
				byte buffer[] = new byte[0x10000]; // 16kB buffer
				int bytesRead = 0;
				try (InputStream in = inputStreamProvider.openInputStream()) {
					while ((bytesRead = in.read(buffer)) != -1)
						outputStream.write(buffer, 0, bytesRead);
				}
			}

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

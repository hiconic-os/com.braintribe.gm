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
package com.braintribe.model.access.collaboration.persistence;

import static com.braintribe.utils.lcd.NullSafe.nonNull;

import java.io.IOException;
import java.io.InputStream;

import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.model.processing.session.api.collaboration.PersistenceAppender;

/**
 * @author peter.gazdik
 */
public class AppendedSnippetImpl implements PersistenceAppender.AppendedSnippet {

	public final long sizeInBytes;
	public final InputStreamProvider inputStreamProvider;

	public AppendedSnippetImpl(long size, InputStreamProvider inputStreamProvider) {
		this.sizeInBytes = nonNull(size, "size");
		this.inputStreamProvider = nonNull(inputStreamProvider, "inputStreamProvider");
	}

	@Override
	public long sizeInBytes() {
		return sizeInBytes;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return inputStreamProvider.openInputStream();
	}

}

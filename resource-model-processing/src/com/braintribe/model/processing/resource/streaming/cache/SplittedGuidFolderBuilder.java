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
package com.braintribe.model.processing.resource.streaming.cache;

import java.io.File;
import java.util.function.Function;



public class SplittedGuidFolderBuilder implements Function<String, File> {
	@Override
	public File apply(String guid) throws RuntimeException {
		if (guid.length() < 4) {
			return new File(guid);
		}
		String level1 = guid.substring(0, 4);
		if (guid.length() < 8) {
			return new File(level1);
		}
		String level2 = guid.substring(4, 8);
		if (guid.length() < 10) {
			return new File(new File(level1), level2);
		}
		String level3 = guid.substring(8, 10);
		return new File(new File(new File(level1), level2), level3);
	}

}

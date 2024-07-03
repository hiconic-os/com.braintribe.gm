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
package com.braintribe.xml.stagedstax.parser.factory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContentExpertFactory implements ContentExpertFactory {
	private Map<String, ContentExpertFactory> tagToFactoryMap = new HashMap<>();
	

	@Override
	public void chainFactory(String tag, ContentExpertFactory factory) {
		tagToFactoryMap.put(tag, factory);
	}

	@Override
	public ContentExpertFactory getChainedFactory(String tag) {
		return tagToFactoryMap.get(tag);
	}

}

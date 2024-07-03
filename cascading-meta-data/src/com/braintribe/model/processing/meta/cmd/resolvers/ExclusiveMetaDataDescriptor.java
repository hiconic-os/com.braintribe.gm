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
package com.braintribe.model.processing.meta.cmd.resolvers;

import static com.braintribe.utils.lcd.CollectionTools2.first;

import java.util.List;

import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

class ExclusiveMetaDataDescriptor extends AbstractMetaDataDescriptor<MetaData> {

	public MetaData defaultMetaData;

	@Override
	protected MetaData ignoreSelectorsValue() {
		return ownerMetaData.isEmpty() ? defaultMetaData : first(first(ownerMetaData)).metaData();
	}

	@Override
	protected MetaData volatileValue(SelectorContext selectorContext) {
		for (List<QualifiedMetaData> md : ownerMetaData) {
			MetaData first = resolutionContext.filterFirstBySelectors(md, selectorContext);
			if (first != null)
				return first;
		}

		return defaultMetaData;
	}

}

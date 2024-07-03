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

import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

class MultiMetaDataDescriptor extends AbstractMetaDataDescriptor<List<MetaData>> {

	@Override
	protected List<MetaData> ignoreSelectorsValue() {
		return ownerMetaData.stream() //
				.flatMap(List::stream) //
				.map(QualifiedMetaData::metaData) //
				.collect(Collectors.toList());
	}

	@Override
	protected List<MetaData> volatileValue(SelectorContext selectorContext) {
		List<MetaData> result = newList();

		for (List<QualifiedMetaData> md : ownerMetaData)
			result.addAll(resolutionContext.filterBySelectors(md, selectorContext));

		return Collections.unmodifiableList(result);
	}

}

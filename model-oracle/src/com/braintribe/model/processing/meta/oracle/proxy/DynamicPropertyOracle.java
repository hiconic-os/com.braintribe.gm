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
package com.braintribe.model.processing.meta.oracle.proxy;

import java.util.stream.Stream;

import com.braintribe.model.generic.proxy.DynamicProperty;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.oracle.BasicQualifiedMetaData;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;
import com.braintribe.model.processing.meta.oracle.empty.EmptyPropertyOracle;

/**
 * @author peter.gazdik
 */
public class DynamicPropertyOracle extends EmptyPropertyOracle {

	private final DynamicProperty property;

	public DynamicPropertyOracle(EntityTypeOracle entityOracle, DynamicProperty property) {
		super(entityOracle);
		this.property = property;
	}

	@Override
	public String getName() {
		return property.getName();
	}

	@Override
	public Property asProperty() {
		return property;
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return property.getMetaData().stream();
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return getMetaData().map(md -> new BasicQualifiedMetaData(md, null));
	}

}

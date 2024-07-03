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
package com.braintribe.model.processing.meta.oracle.empty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.braintribe.model.processing.meta.oracle.PropertyOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;

/**
 * @author peter.gazdik
 */
public class EmptyPropertyOracle implements PropertyOracle {

	private final EntityTypeOracle entityOracle;

	public EmptyPropertyOracle(EntityTypeOracle entityOracle) {
		this.entityOracle = entityOracle;
	}

	@Override
	public EntityTypeOracle getEntityTypeOracle() {
		return entityOracle;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public GmProperty asGmProperty() {
		return null;
	}

	@Override
	public Property asProperty() {
		return null;
	}

	@Override
	public List<GmPropertyInfo> getGmPropertyInfos() {
		return Collections.emptyList();
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return Stream.empty();
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return Stream.empty();
	}

	@Override
	public Object getInitializer() {
		return null;
	}

}

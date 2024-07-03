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
package com.braintribe.model.processing.meta.oracle;

import static com.braintribe.utils.lcd.CollectionTools2.nullSafe;

import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmPropertyInfo;
import com.braintribe.model.processing.meta.oracle.flat.FlatProperty;

/**
 * @author peter.gazdik
 */
public class BasicPropertyOracle implements PropertyOracle {

	private final BasicEntityTypeOracle entityOracle;
	private final FlatProperty flatProperty;

	private Object initializer;

	public BasicPropertyOracle(BasicEntityTypeOracle entityOracle, FlatProperty flatProperty) {
		this.entityOracle = entityOracle;
		this.flatProperty = flatProperty;
	}

	@Override
	public EntityTypeOracle getEntityTypeOracle() {
		return entityOracle;
	}

	@Override
	public String getName() {
		return flatProperty.gmProperty.getName();
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return getGmPropertyInfos().stream().flatMap(gmPropertyInfo -> nullSafe(gmPropertyInfo.getMetaData()).stream());
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return getGmPropertyInfos().stream().flatMap(QualifiedMetaDataTools::ownMetaData);
	}

	@Override
	public List<GmPropertyInfo> getGmPropertyInfos() {
		return flatProperty.infos;
	}

	@Override
	public GmProperty asGmProperty() {
		return flatProperty.gmProperty;
	}

	@Override
	public Property asProperty() {
		return entityOracle.<EntityType<?>> asType().getProperty(flatProperty.gmProperty.getName());
	}

	@Override
	public Object getInitializer() {
		if (initializer == null)
			initializer = entityOracle.getSuperTypes().transitive().includeSelf().asEntityTypeOracles().stream() //
					.map(eto -> eto.findProperty(flatProperty.gmProperty)) //
					.filter(po -> po != null) //
					.flatMap(po -> po.getGmPropertyInfos().stream()) //
					.map(GmPropertyInfo::getInitializer) //
					.filter(i -> i != null) //
					.findFirst() //
					.orElse(void.class);

		return initializer == void.class ? null : initializer;
	}

}

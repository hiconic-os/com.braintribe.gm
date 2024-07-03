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

import com.braintribe.model.generic.proxy.DynamicEntityType;
import com.braintribe.model.generic.proxy.DynamicProperty;
import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.index.ConcurrentCachedIndex;
import com.braintribe.model.processing.meta.oracle.BasicQualifiedMetaData;
import com.braintribe.model.processing.meta.oracle.EntityTypeProperties;
import com.braintribe.model.processing.meta.oracle.ModelOracle;
import com.braintribe.model.processing.meta.oracle.PropertyOracle;
import com.braintribe.model.processing.meta.oracle.QualifiedMetaData;
import com.braintribe.model.processing.meta.oracle.empty.EmptyEntityTypeOracle;

/**
 * @author peter.gazdik
 */
public class DynamicEntityTypeOracle extends EmptyEntityTypeOracle {

	private final DynamicEntityType entityType;

	protected final DynamicPropertyOraclesIndex propertyTypeOracles = new DynamicPropertyOraclesIndex();

	public DynamicEntityTypeOracle(ModelOracle modelOracle, DynamicEntityType entityType) {
		super(modelOracle);
		this.entityType = entityType;
	}

	class DynamicPropertyOraclesIndex extends ConcurrentCachedIndex<String, PropertyOracle> {
		@Override
		protected PropertyOracle provideValueFor(String propertyName) {
			DynamicProperty property = (DynamicProperty) entityType.findProperty(propertyName);
			return property != null ? new DynamicPropertyOracle(DynamicEntityTypeOracle.this, property) : emptyPropertyOracle;
		}
	}

	@Override
	public EntityTypeProperties getProperties() {
		throw new UnsupportedOperationException("Method 'DynamicEntityTypeOracle.getProperties' is not supported!");
	}

	@Override
	public PropertyOracle findProperty(String propertyName) {
		PropertyOracle result = propertyTypeOracles.acquireFor(propertyName);
		return result == emptyPropertyOracle ? null : result;
	}

	@Override
	public Stream<MetaData> getMetaData() {
		return entityType.getMetaData().stream();
	}

	@Override
	public Stream<MetaData> getPropertyMetaData() {
		return entityType.getPropertyMetaData().stream();
	}

	@Override
	public Stream<QualifiedMetaData> getQualifiedMetaData() {
		return getMetaData().map(md -> new BasicQualifiedMetaData(md, null));
	}
	
	@Override
	public Stream<QualifiedMetaData> getQualifiedPropertyMetaData() {
		return getPropertyMetaData().map(md -> new BasicQualifiedMetaData(md, null));
	}

	@Override
	public boolean hasProperty(String propertyName) {
		return entityType.findProperty(propertyName) != null;
	}

	@Override
	public final <T extends CustomType> T asType() {
		return (T) entityType;
	}

}

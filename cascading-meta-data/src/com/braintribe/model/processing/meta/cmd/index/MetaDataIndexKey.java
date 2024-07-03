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
package com.braintribe.model.processing.meta.cmd.index;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.processing.meta.cmd.index.MetaDataIndexStructure.MdIndex;

/**
 * Key of a meta-data index (see {@link MdIndex}).
 */
public final class MetaDataIndexKey {

	private final EntityType<? extends MetaData> mdType;
	private final boolean inheritableOnly;

	public static MetaDataIndexKey forInherited(EntityType<? extends MetaData> metaDataType) {
		return new MetaDataIndexKey(metaDataType, true);
	}

	public static MetaDataIndexKey forAll(EntityType<? extends MetaData> metaDataType) {
		return new MetaDataIndexKey(metaDataType, false);
	}

	private MetaDataIndexKey(EntityType<? extends MetaData> mdType, boolean inheritableOnly) {
		this.mdType = mdType;
		this.inheritableOnly = inheritableOnly;
	}

	public EntityType<? extends MetaData> mdType() {
		return mdType;
	}

	public boolean inheritableOnly() {
		return inheritableOnly;
	}

	public MetaDataIndexKey indexKeyForAll() {
		return new MetaDataIndexKey(mdType, false);
	}

	@Override
	public boolean equals(Object obj) {
		/* This may seem weird, but basically the implementation of this artifact should ensure that the exception is
		 * never thrown, i.e. this class should be used in such way that it is only being check for equality with other
		 * instances of this class. Therefore, for performance reasons, we do not make an instanceof check, but
		 * optimistically assume we can do the cast right away. */
		try {
			MetaDataIndexKey otherKey = (MetaDataIndexKey) obj;
			return mdType == otherKey.mdType && inheritableOnly == otherKey.inheritableOnly;

		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * mdType.hashCode() + (inheritableOnly ? 1231 : 1237);
	}

}

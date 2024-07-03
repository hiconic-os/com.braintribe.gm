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

import java.util.List;
import java.util.stream.Stream;

import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.info.GmEnumConstantInfo;

/**
 * @author peter.gazdik
 */
public interface EnumConstantOracle {

	/** Returns the {@link EnumTypeOracle} from which this {@link EnumConstantOracle} was returned. */
	EnumTypeOracle getEnumTypeOracle();

	GmEnumConstant asGmEnumConstant();

	Enum<?> asEnum();

	/**
	 * Returns a list of {@link GmEnumConstantInfo} declared for this constant.
	 */
	List<GmEnumConstantInfo> getGmEnumConstantInfos();

	/** Returns the meta-data from the {@link GmEnumConstantInfo}s returned by {@link #getGmEnumConstantInfos()}. */
	Stream<MetaData> getMetaData();

	/** Qualified version of {@link #getMetaData()}. */
	Stream<QualifiedMetaData> getQualifiedMetaData();

}

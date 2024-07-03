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

import java.util.stream.Stream;

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.data.HasMetaData;
import com.braintribe.model.meta.info.GmEntityTypeInfo;
import com.braintribe.model.meta.info.GmEnumTypeInfo;

/**
 * @author peter.gazdik
 */
public interface QualifiedMetaDataTools {

	static <M extends GmModelElement & HasMetaData> Stream<QualifiedMetaData> ownMetaData(M modelElementWithMd) {
		return nullSafe(modelElementWithMd.getMetaData()).stream().map(md -> new BasicQualifiedMetaData(md, modelElementWithMd));
	}

	static Stream<QualifiedMetaData> modelEnumMetaData(GmMetaModel model) {
		return nullSafe(model.getEnumTypeMetaData()).stream().map(md -> new BasicQualifiedMetaData(md, model));
	}

	static Stream<QualifiedMetaData> modelConstantMetaData(GmMetaModel model) {
		return nullSafe(model.getEnumConstantMetaData()).stream().map(md -> new BasicQualifiedMetaData(md, model));
	}

	static Stream<QualifiedMetaData> entityPropertyMetaData(GmEntityTypeInfo eti) {
		return nullSafe(eti.getPropertyMetaData()).stream().map(md -> new BasicQualifiedMetaData(md, eti));
	}

	static Stream<QualifiedMetaData> enumConstantMetaData(GmEnumTypeInfo eti) {
		return nullSafe(eti.getEnumConstantMetaData()).stream().map(md -> new BasicQualifiedMetaData(md, eti));
	}

}

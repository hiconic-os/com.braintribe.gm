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
package com.braintribe.codec.marshaller.dom;

import java.util.Set;

import org.w3c.dom.Document;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.coder.DeferredDecoder;
import com.braintribe.codec.marshaller.dom.coder.entity.EntityDomCodingPreparation;
import com.braintribe.codec.marshaller.dom.coder.entity.PropertyAbsenceHelper;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;

public interface DomDecodingContext {
	
	Document getDocument();

	TypeInfo getTypeInfoByKey(String typeKey) throws CodecException;
	
	void registerTypeInfo(TypeInfo4Read typeInfo);
	
	EntityRegistration acquireEntity(String ref) throws CodecException;

	AbsenceInformation getAbsenceInformationForMissingProperties();

	PropertyAbsenceHelper providePropertyAbsenceHelper();

	void appendDeferredDecoder(DeferredDecoder coder);

	int getMaxDeferred();

	DeferredDecoder getFirstDeferredDecoder();

	EntityDomCodingPreparation getEntityDomCodingPreparation(EntityType<?> entityType) throws CodecException;

	Set<String> getRequiredTypes();
}

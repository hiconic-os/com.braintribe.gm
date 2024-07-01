// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.codec.marshaller.stax;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.codec.marshaller.api.DecodingLenience;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.CustomType;

public interface DecodingContext {
	<T extends CustomType> T findType(String typeSignature);
	TypeInfo4Read getTypeInfoByKey(String key);
	void registerTypeInfo(TypeInfo4Read typeInfo); 
	int getVersion();
	DateTimeFormatter getDateFormat();
	PropertyAbsenceHelper providePropertyAbsenceHelper();
	AbsenceInformation getAbsenceInformationForMissingProperties();
	GenericEntity lookupEntity(String ref);
	boolean isEnhanced();
	void register(GenericEntity entity, String idString) throws MarshallException;
	Consumer<Set<String>> getRequiredTypesReceiver();
	DecodingLenience getDecodingLenience();
	void addEntityRegistrationListener(String referenceId, EntityRegistrationListener deferredProcessor);
	DecoderFactoryContext getDecoderFactoryContext();
	EntityRegistration acquireEntity(String ref) throws MarshallException;
	void pushDelegateDecoder(Decoder valueDecoder);
	void popDelegateDecoder();
}

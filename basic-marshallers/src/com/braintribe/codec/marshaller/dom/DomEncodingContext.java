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

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.braintribe.codec.CodecException;
import com.braintribe.codec.marshaller.dom.coder.DeferredEncoder;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.GenericModelType;

public interface DomEncodingContext {
	
	Document getDocument();

	TypeInfo registerRequiredType(GenericModelType type);
	
	String lookupQualifiedId(GenericEntity entity) throws CodecException;

	boolean shouldWriteAbsenceInformation();

	boolean isSimpleAbsenceInformation(AbsenceInformation absenceInformation);

	Collection<TypeInfo> getRequiredTypeInfos();
	
	void appendDeferredEncoder(DeferredEncoder coder);

	int getMaxDeferred(); 
	
	void appendToPool(Element element);
	
	void setPool(Element element);

	DeferredEncoder getFirstDeferredEncoder();

}

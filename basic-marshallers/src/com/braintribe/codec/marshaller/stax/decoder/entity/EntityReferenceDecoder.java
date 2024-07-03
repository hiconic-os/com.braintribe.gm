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
package com.braintribe.codec.marshaller.stax.decoder.entity;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.model.generic.GenericEntity;

public class EntityReferenceDecoder extends Decoder {
	private boolean nullAware;
	
	public EntityReferenceDecoder(boolean nullAware) {
		super();
		this.nullAware = nullAware;
	}

	@Override
	public void begin(Attributes attributes) throws MarshallException {
		String referenceId = attributes.getValue("ref");
		
		if (referenceId == null) {
			if (nullAware && elementName.equals("null"))
				parent.notifyValue(this, null);
			else 
				throw new MarshallException("entity element must have a ref attribute");
		}
		else {
			GenericEntity entity = decodingContext.lookupEntity(referenceId);
			
			if (entity != null) {
				parent.notifyValue(this, entity);
			}
			else {
				parent.notifyForwardEntity(this, referenceId);
			}
		}
	}
	
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String elementName, Attributes attributes) throws MarshallException {
		throw new MarshallException("no child element " + elementName + " is allowed for entity element with ref attribute");
	}
}

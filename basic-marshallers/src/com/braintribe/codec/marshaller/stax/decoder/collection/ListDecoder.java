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
package com.braintribe.codec.marshaller.stax.decoder.collection;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.EntityRegistrationListener;
import com.braintribe.codec.marshaller.stax.decoder.Decoder;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactory;
import com.braintribe.codec.marshaller.stax.factory.DecoderFactoryContext;
import com.braintribe.codec.marshaller.stax.factory.ObjectDecoderFactory;
import com.braintribe.model.generic.GenericEntity;

public class ListDecoder extends Decoder {
	private List<Object> list;
	private DecoderFactory elementDecoderFactory;
	private int childCount;
	
	public ListDecoder(DecoderFactory elementDecoderFactory) {
		this.elementDecoderFactory = elementDecoderFactory;
	}
	
	public ListDecoder() {
		this.elementDecoderFactory = ObjectDecoderFactory.INSTANCE;
	}
	
	@Override
	public Decoder newDecoder(DecoderFactoryContext context, String _elementName, Attributes attributes) throws MarshallException {
		childCount++;
		return elementDecoderFactory.newDecoder(context, _elementName, attributes);
	}

	@Override
	public void end() throws MarshallException {
		if (childCount == 0 && elementName.equals("null"))
			parent.notifyValue(this, null);
		else
			parent.notifyValue(this, getList());
	}

	private List<Object> getList() {
		if (list == null) {
			list = new ArrayList<Object>();
		}

		return list;
	}
	@Override
	public void notifyValue(Decoder origin, Object value) {
		getList().add(value);
	}
	
	@Override
	public void notifyForwardEntity(Decoder origin, String referenceId) {
		// remember size as index for later update of the list position
		final List<Object> localList = getList();
		final int index = localList.size();
		
		// add null a place holder element to grow the list 
		localList.add(null);
		
		decodingContext.addEntityRegistrationListener(referenceId, new EntityRegistrationListener() {
			@Override
			public void onEntityRegistered(GenericEntity entity) throws MarshallException {
				// update the list when the actual value is delivered
				localList.set(index, entity);
			}
		});
	}
}

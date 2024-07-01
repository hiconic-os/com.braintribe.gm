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
package com.braintribe.codec.marshaller.common;

import java.io.InputStream;
import java.io.OutputStream;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;

public class DummyMarshaller implements Marshaller {

	protected final static DummyMarshaller instance = new DummyMarshaller();
	
	@Override
	public void marshall(OutputStream out, Object value) throws MarshallException {
		//Do nothing
	}

	@Override
	public Object unmarshall(InputStream in) throws MarshallException {
		//Do nothing
		return null;
	}

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {
		//Do nothing
	}

	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {
		//Do nothing
		return null;
	}

}

//Not functional but just to later be able to check type of marshaller via instanceof
class CustomTestMarshaller implements Marshaller {

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {}

	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {return null;}
	
}

//Not functional but just to later be able to check type of marshaller via instanceof
class CoreTestMarshaller implements Marshaller {
	
	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {}
	
	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {return null;}
	
}
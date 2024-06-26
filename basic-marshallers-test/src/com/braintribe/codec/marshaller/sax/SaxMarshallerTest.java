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
package com.braintribe.codec.marshaller.sax;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class SaxMarshallerTest {

	@Test
	public void testXXEAttack() throws Exception {
		
		SaxMarshaller<Object> saxMarshaller = new SaxMarshaller<Object>();
		saxMarshaller.setWriteRequiredTypes(true);
		
		/* This was for testing... this point should not be reached anymore. Keeping this for future purposes.
		User user = User.T.create();
		user.setFirstName("John");
		user.setLastName("Doe");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		saxMarshaller.marshall(baos, user);

		String text = new String(baos.toString("UTF-8"));
		System.out.println(text);
		*/
		
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		sb.append("<!DOCTYPE lolz [\n");
		sb.append("<!ENTITY lol SYSTEM \"file:///\">\n");
		sb.append("]>\n");
		sb.append("<?gm-xml version=\"3\"?><gm-data>\n");
		sb.append("<required-types>\n");
		sb.append("<type>com.braintribe.model.user.User</type>\n");
		sb.append("</required-types>\n");
		sb.append("<root-value>\n");
		sb.append("<entity ref=\"0\"/>\n");
		sb.append("</root-value>\n");
		sb.append("<pool>\n");
		sb.append("<entity id=\"0\" type=\"com.braintribe.model.user.User\">\n");
		sb.append("<property name=\"firstName\">\n");
		sb.append("<string>John</string>\n");
		sb.append("</property>\n");
		sb.append("<property name=\"lastName\">\n");
		sb.append("<string>Doe</string>\n");
		sb.append("</property>\n");
		sb.append("<property name=\"password\">\n");
		sb.append("<string>&lol;</string>\n");
		sb.append("</property>\n");
		sb.append("</entity>\n");
		sb.append("</pool>\n");
		sb.append("</gm-data>\n");
		ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
	
		try {
			saxMarshaller.unmarshall(bais);
			throw new AssertionError("XXE attack was successful.");
		} catch(Exception e) {
			//This is expected
			return;
		}
	}
	
}

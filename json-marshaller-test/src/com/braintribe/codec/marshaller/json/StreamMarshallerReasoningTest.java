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
package com.braintribe.codec.marshaller.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;

public class StreamMarshallerReasoningTest {
	@Test 
	public void testPropertyTypeMismatchReasoning() throws FileNotFoundException, IOException {
		testParseError("res/property-value-type-mismatch.json", "parsing json with a property value type mismatch");
	}
	
	@Test 
	public void testCollectionElementTypeMismatchReasoning() throws FileNotFoundException, IOException {
		testParseError("res/collection-element-type-mismatch.json", "parsing json with a collection element type mismatch");
	}
	
	@Test 
	public void testSyntaxErrorReasoning() throws FileNotFoundException, IOException {
		testParseError("res/syntax-error.json", "parsing json with a syntax error");
	}
	
	@Test 
	public void testUnknownTypeReasoning() throws FileNotFoundException, IOException {
		testParseError("res/unknown-type.json", "parsing json with an unknown type");
	}
	
	@Test 
	public void testUnknownPropertyReasoning() throws FileNotFoundException, IOException {
		testParseError("res/unknown-property.json", "parsing json with an unknown property");
	}
	
	private void testParseError(String fileName, String errorCase) throws FileNotFoundException, IOException {
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		
		try (InputStream in = new FileInputStream(fileName)) {
			Maybe<Object> maybe = marshaller.unmarshallReasoned(in);
			
			if (!maybe.isUnsatisfiedBy(ParseError.T))
				Assertions.fail("Missing ParseError reason when " + errorCase);
			
			System.out.println(maybe.whyUnsatisfied().asString());
		}
	}

}

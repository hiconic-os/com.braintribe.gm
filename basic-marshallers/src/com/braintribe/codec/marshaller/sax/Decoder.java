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
package com.braintribe.codec.marshaller.sax;

import org.xml.sax.Attributes;

import com.braintribe.codec.marshaller.api.MarshallException;

public abstract class Decoder {
	public Decoder predecessor;
	public Decoder nextToBeEvaluated;
	
	public abstract void appendCharacters(char [] characters, int s, int l);
	public abstract void begin(DecodingContext context, Attributes attributes) throws MarshallException;
	public abstract void end(DecodingContext context) throws MarshallException;
	public abstract void onDescendantEnd(DecodingContext context, Decoder decoder) throws MarshallException;
}

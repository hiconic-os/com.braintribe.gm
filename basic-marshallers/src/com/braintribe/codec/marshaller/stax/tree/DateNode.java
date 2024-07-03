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
package com.braintribe.codec.marshaller.stax.tree;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.DateFormats;
import com.braintribe.codec.marshaller.stax.PrettinessSupport;
import com.braintribe.utils.DateTools;

public class DateNode extends ValueStaxNode {
	private static final char[] endElement = "</T>".toCharArray();
	private static final char[] startPropElementClose = "'>".toCharArray();
	private static final char[] startPropElement = "<T p='".toCharArray();
	private static final char[] startElement = "<T>".toCharArray();

	private Date value;
	
	public DateNode(Date value) {
		super();
		this.value = value;
	}

	@Override
	public void write(Writer writer, PrettinessSupport prettinessSupport, int indent) throws IOException, MarshallException {
		writer.write(startElement);
		writer.write(DateTools.encode(value, DateFormats.dateFormat));
		writer.write(endElement);
	}
	
	@Override
	public void write(Writer writer, PrettinessSupport prettinessSupport, String propertyName, int indent) throws IOException, MarshallException {
		writer.write(startPropElement);
		writer.write(propertyName);
		writer.write(startPropElementClose);
		writer.write(DateTools.encode(value, DateFormats.dateFormat));
		writer.write(endElement);
	}
}

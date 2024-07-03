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

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.stax.PrettinessSupport;

public class ListNode extends ValueStaxNode {
	private static final char[] startElement = "<L>".toCharArray();
	private static final char[] endElement = "</L>".toCharArray();
	private static final char[] emptyElement = "<L/>".toCharArray();
	
	private static final char[] startPropElementClose = "'>".toCharArray();
	private static final char[] startPropElement = "<L p='".toCharArray();
	private final ValueStaxNode[] nodes;
	
	public ListNode(ValueStaxNode[] nodes) {
		this.nodes = nodes;
	}

	@Override
	void write(Writer writer, PrettinessSupport prettinessSupport, String propertyName, int indent) throws IOException, MarshallException {
		writer.write(startPropElement);
		writer.write(propertyName);
		writer.write(startPropElementClose);

		finishElement(writer, prettinessSupport, indent);
	}

	@Override
	public void write(Writer writer, PrettinessSupport prettinessSupport, int indent) throws IOException, MarshallException {
		if (nodes.length == 0) {
			writer.write(emptyElement);

		} else {
			writer.write(startElement);

			finishElement(writer, prettinessSupport, indent);
		}
	}

	private void finishElement(Writer writer, PrettinessSupport prettinessSupport, int indent) throws IOException {
		int elementIndent = indent + 1;
		for (ValueStaxNode node: nodes) {
			prettinessSupport.writeLinefeed(writer, elementIndent);
			node.write(writer, prettinessSupport, elementIndent);
		}
		
		prettinessSupport.writeLinefeed(writer, indent);
		writer.write(endElement);
	}
}

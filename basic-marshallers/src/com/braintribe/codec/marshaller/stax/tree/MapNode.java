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

public class MapNode extends ValueStaxNode {
	private static final char[] startPropElement = "<M p='".toCharArray();
	private static final char[] startPropElementClose = "'>".toCharArray();
	private static final char[] startElement = "<M>".toCharArray();

	private static final char[] endElement = "</M>".toCharArray();

	private static final char[] emptyElement = "<M/>".toCharArray();

	private static final char[] startEntryElement = "<m>".toCharArray();
	private static final char[] endEntryElement = "</m>".toCharArray();

	private final ValueStaxNode[] nodes;

	public MapNode(ValueStaxNode[] nodes) {
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
		int entryIndent = indent + 1;
		int l = nodes.length;
		for (int i = 0; i < l;) {
			prettinessSupport.writeLinefeed(writer, entryIndent);
			writer.write(startEntryElement);
			nodes[i++].write(writer, prettinessSupport, entryIndent);
			nodes[i++].write(writer, prettinessSupport, entryIndent);
			writer.write(endEntryElement);
		}

		prettinessSupport.writeLinefeed(writer, indent);
		writer.write(endElement);
	}

}

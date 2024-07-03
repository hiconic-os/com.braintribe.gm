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

public class StringNode extends ValueStaxNode {
	private static final char[] endElement = "</s>".toCharArray();
	private static final char[] startPropElementClose = "'>".toCharArray();
	private static final char[] startPropElement = "<s p='".toCharArray();
	private static final char[] startElement = "<s>".toCharArray();	

	private String string;
	
	public StringNode(String string) {
		super();
		this.string = string;
	}

	@Override
	public void write(Writer writer, PrettinessSupport prettinessSupport, int indent) throws IOException, MarshallException {
		writer.write(startElement);
		writeEscaped(writer, string);
		writer.write(endElement);
	}
	
	@Override
	public void write(Writer writer, PrettinessSupport prettinessSupport, String propertyName, int indent) throws IOException, MarshallException {
		writer.write(startPropElement);
		writer.write(propertyName);
		writer.write(startPropElementClose);
		writeEscaped(writer, string);
		writer.write(endElement);
	}
	
	private static final char[][] ESCAPES = new char[63][];
    
    static {
    	ESCAPES['<'] = "&lt;".toCharArray();
    	ESCAPES['>'] = "&gt;".toCharArray();
    	ESCAPES['&'] = "&amp;".toCharArray();
    	ESCAPES['\r'] = "&#13;".toCharArray();
    }
    
    private static final char[][] ATTRIBUTE_ESCAPES = new char[63][];
    
    static {
    	ATTRIBUTE_ESCAPES['<'] = "&lt;".toCharArray();
    	ATTRIBUTE_ESCAPES['>'] = "&gt;".toCharArray();
    	ATTRIBUTE_ESCAPES['&'] = "&amp;".toCharArray();
    	ATTRIBUTE_ESCAPES['\r'] = "&#13;".toCharArray();
    	ATTRIBUTE_ESCAPES['\''] = "&apos;".toCharArray();
    }

    public static void writeEscaped(Writer writer, String string) throws IOException {
    	writeEscaped(writer, string, ESCAPES);
    }
    
    public static void writeEscapedAttribute(Writer writer, String string) throws IOException {
    	writeEscaped(writer, string, ATTRIBUTE_ESCAPES);
    }
    
    public static void writeEscaped(Writer writer, String string, char[][] escapes) throws IOException {
    	int len = string.length();
    	int s = 0;
    	int i = 0;
    	char esc[] = null;
    	for (; i < len; i++) {
    		char c = string.charAt(i);
    		
    		if (c < 63) {
    			esc = escapes[c];
    			if (esc != null) {
    				writer.write(string, s, i - s);
    				writer.write(esc);
    				s = i + 1;
    			}
    		}
    	}
    	if (i > s) {
    		if (s == 0)
    			writer.write(string);
    		else
    			writer.write(string, s, i - s);
    	}
    }
}

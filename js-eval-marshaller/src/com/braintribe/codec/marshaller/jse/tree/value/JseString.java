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
package com.braintribe.codec.marshaller.jse.tree.value;

import java.io.IOException;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.jse.CountingWriter;
import com.braintribe.codec.marshaller.jse.tree.JseNode;

public class JseString extends JseNode {
	private String value;

	public JseString(String value) {
		super();
		this.value = value;
	}

    private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static final char[][] ESCAPES = new char[128][];
    
    static {
    	ESCAPES['"'] = "\\\"".toCharArray();
    	ESCAPES['\\'] = "\\\\".toCharArray();
    	ESCAPES['\t'] = "\\t".toCharArray();
    	ESCAPES['\f'] = "\\f".toCharArray();
    	ESCAPES['\n'] = "\\n".toCharArray();
    	ESCAPES['\r'] = "\\r".toCharArray();
    	
    	for (int i = 0; i < 32; i++) {
    		if (ESCAPES[i] == null)
    			ESCAPES[i] = ("\\u00" + HEX_CHARS[i >> 4] + HEX_CHARS[i & 0xF]).toCharArray();
    	}
    }

	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		writer.write('"');
		String string = value;
    	int len = string.length();
    	int s = 0;
    	int i = 0;
    	char esc[] = null;
    	for (; i < len; i++) {
    		char c = string.charAt(i);
    		
    		if (c < 128) {
    			esc = ESCAPES[c];
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
    	writer.write('"');
    }


}

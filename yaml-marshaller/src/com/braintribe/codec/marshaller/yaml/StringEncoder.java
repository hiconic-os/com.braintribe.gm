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
package com.braintribe.codec.marshaller.yaml;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.events.Characters;

import com.braintribe.utils.stream.PrintStreamWriter;

public class StringEncoder {
	private static final List<String> y_keywords = Arrays.asList("y", "yes");
	private static final List<String> Y_keywords = Arrays.asList("Y", "Yes", "YES");
	private static final List<String> n_keywords = Arrays.asList("n", "no");
	private static final List<String> N_keywords = Arrays.asList("N", "No", "NO");
	private static final List<String> t_keywords = Arrays.asList("true");
	private static final List<String> T_keywords = Arrays.asList("True", "TRUE");
	private static final List<String> f_keywords = Arrays.asList("false");
	private static final List<String> F_keywords = Arrays.asList("False", "FALSE");


	private static Collection<String> keywords_1_1 = new HashSet<>(Arrays.asList( 
			"y", 
			"Y", 
			"yes", 
			"Yes", 
			"YES", 
			"n", 
			"N", 
			"no", 
			"No", 
			"NO", 
			"true", 
			"True", 
			"TRUE", 
			"false", 
			"False", 
			"FALSE",
			"on", 
			"On", 
			"ON", 
			"off", 
			"Off", 
			"OFF" 
	));
	
	private static Collection<String> keywords_1_2 = Arrays.asList( 
			"true", 
			"false" 
	);
	
	private static String forbiddenPlainChars = ":#\t[]{},\n";
	private static String forbiddenPlainStartChars = "-?:,[]{}#&*!|>'\"%@`\n.";
	
	
	private Collection<String> keywords = keywords_1_1;
	private int plainStringMaxLength = 255;
	private boolean noPlain;
	
	public void write(Writer writer, CharSequence indent, String s) throws IOException {
		if (canBePlain(s)) {
			writer.write(s);
		}
		else {
			writer.write('"');
			writeEscaped(indent, writer, s);
			writer.write('"');
		}
	}

	private boolean canBePlain(String s) {
		return !noPlain && s.length() < plainStringMaxLength && !keywords.contains(s) && !containsSpecialChars(s);
	}
	
	private boolean containsSpecialChars(String s) {
		int length = s.length();
		
		String forbidden = forbiddenPlainStartChars;
		
		
		for (int i = 0; i < length; i++) {
			
			char c = s.charAt(i);
			if (forbidden.indexOf(c) != -1)
				return true; 
			
			if (i == 0) {
				if (Character.isDigit(c))
					return true;
				
				forbidden = forbiddenPlainChars;
			}
		}
		
		return false;
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

    private static int write(CharSequence indent, String str, int s, int l, int indexInLine) {
    	return 0;
    }
    
    public static void writeEscaped(CharSequence indent, Writer writer, String string) throws IOException {
    	int len = string.length();
    	int s = 0;
    	int i = 0;
    	int charsInLine = 0;
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
    }


	public static void main(String[] args) {
		try {
			String cases[] = {
				"!",
				"Hallo-Welt",
				"Hallo\nWelt",
				"-Hallo Welt",
				"1.2",
				"12",
				".2"
				
			};
			
			StringEncoder encoder = new StringEncoder();
			
			Writer writer = new PrintStreamWriter(System.out, true);
			
			for (int i = 0; i < cases.length; i++) {
				String s = cases[i];
				
				encoder.write(writer, "", s);
				writer.write('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

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
package com.braintribe.model.generic.reflection;

public class JavaStringLiteralEscape {
	/**
	 * Escapes string content to be a valid string literal.
	 * 
	 * @return an escaped version of <code>unescaped</code>, suitable for being enclosed in double quotes in Java source
	 */
	public static String escape(String unescaped) {
		int extra = 0;
		for (int in = 0, n = unescaped.length(); in < n; ++in) {
			switch (unescaped.charAt(in)) {
				case '\0':
				case '\n':
				case '\r':
				case '\"':
				case '\\':
					++extra;
					break;
			}
		}

		if (extra == 0) {
			return unescaped;
		}

		char[] oldChars = unescaped.toCharArray();
		char[] newChars = new char[oldChars.length + extra];
		for (int in = 0, out = 0, n = oldChars.length; in < n; ++in, ++out) {
			char c = oldChars[in];
			switch (c) {
				case '\0':
					newChars[out++] = '\\';
					c = '0';
					break;
				case '\n':
					newChars[out++] = '\\';
					c = 'n';
					break;
				case '\r':
					newChars[out++] = '\\';
					c = 'r';
					break;
				case '\"':
					newChars[out++] = '\\';
					c = '"';
					break;
				case '\\':
					newChars[out++] = '\\';
					c = '\\';
					break;
			}
			newChars[out] = c;
		}

		return String.valueOf(newChars);
	}
}

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

import java.io.IOException;
import java.io.Writer;

public class HighPrettinessSupport extends PrettinessSupport {
	private static final char[] linefeed = "\n                    ".toCharArray();
	
	@Override
	public void writeLinefeed(Writer writer, int indent) throws IOException {
		if (indent > 20)
			indent = 20;
		writer.write(linefeed, 0, indent + 1);
	}
	
	@Override
	public int getMaxIndent() {
		return 20;
	}
}

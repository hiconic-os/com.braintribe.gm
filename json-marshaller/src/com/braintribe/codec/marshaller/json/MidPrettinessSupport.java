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
package com.braintribe.codec.marshaller.json;

import java.io.IOException;
import java.io.Writer;

public class MidPrettinessSupport extends PrettinessSupport {
	private static final char[] linefeed = "\n          ".toCharArray();
	
	@Override
	public void writeLinefeed(Writer writer, int indent) throws IOException {
		if (indent > 10)
			indent = 10;
		writer.write(linefeed, 0, indent + 1);
	}
	
	@Override
	public int getMaxIndent() {
		return 10;
	}
}

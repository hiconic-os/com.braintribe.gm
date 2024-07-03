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
package com.braintribe.codec.marshaller.jse.tree;

import java.io.IOException;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.jse.CountingWriter;

public class JseStatementSequence extends JseNode {
	
	protected JseScriptNode anchor = new JseScriptNode();
	protected JseScriptNode last = anchor;
	
	public void append(JseNode node) {
		JseScriptNode listNode = new JseScriptNode();
		listNode.node = node;
		last.next = listNode;
		last = listNode;
	}
	
	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		JseScriptNode listNode = anchor.next;

		int lastCount = writer.getCount();
		
		while (listNode != null) {
			int d = writer.getCount() - lastCount;
			if (d > 160) {
				writer.writeBreakingPoint();
				lastCount = writer.getCount();
			}
			else if (d == 0) {
				writer.writeBreakingPoint();
			}
			
			listNode.node.write(writer);
			listNode = listNode.next;
		}
	}
}

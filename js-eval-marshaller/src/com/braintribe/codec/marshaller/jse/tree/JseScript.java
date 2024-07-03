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

public class JseScript extends JseNode {
	
	protected JseScriptNode anchor = new JseScriptNode();
	protected JseScriptNode last = anchor;
	
	public void append(JseNode node) {
		JseScriptNode listNode = new JseScriptNode();
		listNode.node = node;
		last.next = listNode;
		last = listNode;
	}
	
	public void append(JseNode node, boolean breakPotential) {
		JseScriptNode listNode = new JseScriptNode();
		listNode.breakPotential = breakPotential;
		listNode.node = node;
		last.next = listNode;
		last = listNode;
	}

	@Override
	public void write(CountingWriter writer) throws MarshallException, IOException {
		JseScriptNode listNode = anchor.next;

		while (listNode != null) {
			if (listNode.breakPotential) {
				writer.writeBreakingPoint();
			}
			else {
				writer.write('\n');
			}
			listNode.node.write(writer);
			listNode = listNode.next;
		}
	}

}

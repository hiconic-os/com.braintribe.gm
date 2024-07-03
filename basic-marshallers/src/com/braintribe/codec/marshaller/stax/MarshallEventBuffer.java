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
package com.braintribe.codec.marshaller.stax;

import java.io.IOException;
import java.io.Writer;

public class MarshallEventBuffer extends MarshallEventSink {
	private static final char[] indent5 = new char[] {' ', ' ', ' ', ' ', ' '};
	private static final char[] indent4 = new char[] {' ', ' ', ' ', ' '};
	private static final char[] indent3 = new char[] {' ', ' ', ' '};
	private static final char[] indent2 = new char[] {' ', ' '};
	private MarshallEvent anchor = new AnchorEvent();
	private MarshallEvent last = anchor;
	private int indent = 2;
	
	@Override
	public void append(MarshallEvent event) throws IOException {
		last.next = event;
		last = event;
	}
	
	public void write(Writer writer) throws IOException {
		MarshallEvent event = anchor.next;
		while (event != null) {
			switch (event.mode){
			case MarshallEvent.MODE_FLOW:
				event.write(writer);
				break;
			case MarshallEvent.MODE_LF:
				writeLineFeed(writer);
				event.write(writer);
				break;
			case MarshallEvent.MODE_LF_INDENT:
				writeLineFeed(writer);
				indent++;
				event.write(writer);
				break;
			case MarshallEvent.MODE_UNINDENT_LF:
				indent--;
				writeLineFeed(writer);
				event.write(writer);
				break;
			}
			
			event = event.next;
		}
	}
	
	private void writeLineFeed(Writer writer) throws IOException {
		writer.write('\n');
		switch (indent) {
		case 0: break;
		case 1: writer.write(' '); break;
		case 2: writer.write(indent2); break;
		case 3: writer.write(indent3); break;
		case 4: writer.write(indent4); break;
		case 5: writer.write(indent5); break;
		default:
			for (int i = 0; i < indent; i++)
				writer.write(' ');
		}
		
	}

	private static class AnchorEvent extends MarshallEvent {
		@Override
		public void write(Writer writer) throws IOException {
			//Nothing to do here
		}
	}
}

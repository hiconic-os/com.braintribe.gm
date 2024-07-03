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
package dev.hiconic.servlet.webrpc.server.multipart;

import java.io.IOException;
import java.io.OutputStream;

import com.braintribe.model.generic.session.OutputStreamProvider;
import com.braintribe.utils.stream.DecoratorOutputStream;

public class PartOutputStreamProvider  implements OutputStreamProvider {
	
	public enum State {
		initial, opened, closed
	}
	
	private PartAcquiring partAcquiring;
	private String bindId;
	private State state = State.initial;
	private OutputStream out;
	
	public PartOutputStreamProvider(PartAcquiring partAcquiring, String bindId) {
		super();
		this.partAcquiring = partAcquiring;
		this.bindId = bindId;
	}
	
	@Override
	public OutputStream openOutputStream() throws IOException {
		return out = new PartDecoratorOutputStream(partAcquiring.openPartStream(bindId));
	}
	
	private class PartDecoratorOutputStream extends DecoratorOutputStream {
		public PartDecoratorOutputStream(OutputStream delegate) {
			super(delegate);
			state = State.opened;
		}
		
		@Override
		public void close() throws IOException {
			state = State.closed;
			super.close();
		}
	}
	
	public State getState() {
		return state;
	}
	
	public String getBindId() {
		return bindId;
	}
	
	public OutputStream getOut() {
		return out;
	}
}

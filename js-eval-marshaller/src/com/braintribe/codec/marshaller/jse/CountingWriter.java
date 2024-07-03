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
package com.braintribe.codec.marshaller.jse;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CountingWriter extends Writer {
	private Writer delegate;
	private int charactersPerBlock;
	private int count;
	private int lastCount;
	private List<Integer> splitPoints = new ArrayList<Integer>();
	
	public CountingWriter(Writer delegate, int charactersPerBlock) {
		super();
		this.delegate = delegate;
		this.charactersPerBlock = charactersPerBlock;
	}
	
	public int getCount() {
		return count;
	}
	
	public List<Integer> getSplitPoints() {
		return splitPoints;
	}

	public void writeBreakingPoint() throws IOException {
		delegate.write('\n');
		count++;
		
		if ((count - lastCount) > charactersPerBlock) {
			//delegate.write(breakingPoint);
			//delegate.write(JseFunctionsImport.functionsImport);
			splitPoints.add(count);
			lastCount = count;
		}
	}
	
	public void write(int c) throws IOException {
		count++;
		delegate.write(c);
	}

	public void write(char[] cbuf) throws IOException {
		count += cbuf.length;
		delegate.write(cbuf);
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		count += len;
		delegate.write(cbuf, off, len);
	}

	public void write(String str) throws IOException {
		count += str.length();
		delegate.write(str);
	}

	public void write(String str, int off, int len) throws IOException {
		count += len;
		delegate.write(str, off, len);
	}

	public Writer append(CharSequence csq) throws IOException {
		count += csq.length();
		return delegate.append(csq);
	}

	public Writer append(CharSequence csq, int start, int end) throws IOException {
		count += end - start;
		return delegate.append(csq, start, end);
	}

	public Writer append(char c) throws IOException {
		count++;
		return delegate.append(c);
	}

	public void flush() throws IOException {
		delegate.flush();
	}

	public void close() throws IOException {
		delegate.close();
	}
}

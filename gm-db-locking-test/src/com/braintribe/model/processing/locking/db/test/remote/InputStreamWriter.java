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
package com.braintribe.model.processing.locking.db.test.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamWriter extends Thread {

	/**
	 * Stream being read
	 */
	protected final InputStream stream;

	public InputStreamWriter(InputStream stream) {
		this.stream = stream;
	}

	/**
	 * Stream the data.
	 */
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			try {
				char[] buffer = new char[4096];
				int n = 0;
				while (-1 != (n = br.read(buffer))) {
					System.out.print(new String(buffer, 0, n));
					System.out.flush();
				}			
			} finally {
				br.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

}

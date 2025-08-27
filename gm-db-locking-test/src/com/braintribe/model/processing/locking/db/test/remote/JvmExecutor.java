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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JvmExecutor {

	public static List<RemoteProcess> executeWorkers(
			int workerCount, 
			int failProbability,
			long maxWait,
			String filePath,
			int iterations) throws Exception {

		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		
		List<RemoteProcess> remoteProcesses = new ArrayList<RemoteProcess>();

		for (int i=0; i<workerCount; ++i) {
			String workerId = "Worker-"+i;
			
			ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, "-DtestMode=remote", WorkerExecutor.class.getName(), 
					"failProbability="+failProbability, 
					"workerId="+workerId,
					"maxWait="+maxWait,
					"file="+filePath,
					"iterations="+iterations);
			processBuilder.inheritIO();
			Process process = processBuilder.start();
			RemoteProcess remoteProcess = new RemoteProcess(process, workerId);
			remoteProcesses.add(remoteProcess);
			
			InputStream inStream = process.getInputStream();
			InputStreamWriter ishStdout = new InputStreamWriter(inStream);
			ishStdout.start();
		}
		
		return remoteProcesses;
		
	}

}

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
package com.braintribe.model.access.smood.collaboration.tools;

import java.io.File;
import java.io.IOException;

import com.braintribe.utils.FileTools;

/**
 * @author peter.gazdik
 */
public class CsaTestTools {

	public static File createWorkingFolder(File prototypFolder) throws IOException {
		File workingFolder = new File(resWork(), prototypFolder.getName());

		if (workingFolder.exists())
			FileTools.deleteDirectoryRecursively(workingFolder);

		FileTools.copyDirectory(prototypFolder, workingFolder);
		
		return workingFolder;
	}

	private static File resWork() throws IOException {
		File res_work = new File("res-work");
		FileTools.ensureDirectoryExists(res_work);
		
		return res_work;
	}

}

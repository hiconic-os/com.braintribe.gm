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
package com.braintribe.model.io.metamodel;

import java.io.File;

import com.braintribe.model.io.metamodel.testbase.MetaModelBuilder;
import com.braintribe.model.meta.GmMetaModel;

/**
 * 
 */
public class MetaModelSourceWriterService_TestRun {

	public static void main(String[] args) {
		GmSourceWriter writerService = new GmSourceWriter();

		writerService.setOutputDirectory(new File("C:/tmp/MetaModel"));
		writerService.setGmMetaModel(getMetaModel());

		try {
			writerService.writeMetaModelToDirectory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static GmMetaModel getMetaModel() {
		return new MetaModelBuilder().buildMetaModel();
	}

}

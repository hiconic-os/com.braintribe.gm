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
package com.braintribe.codec.json.genericmodel;

import java.io.File;

import org.junit.Test;

import com.braintribe.utils.IOTools;


public class GenericModelJsonCodecAiTest {
	@Test
	public void testDumps() throws Exception {
		File dataFile = new File("./data/ai.json");
		
		String encodedData = IOTools.slurp(dataFile, "UTF-8");
		GenericModelJsonStringCodec<Object> codec = new GenericModelJsonStringCodec<Object>();
		codec.setAssignAbsenceInformationForMissingProperties(true);
		
		Object value = codec.decode(encodedData);
		
		String reencoded = codec.encode(value);
		System.out.println(reencoded);
	}
}

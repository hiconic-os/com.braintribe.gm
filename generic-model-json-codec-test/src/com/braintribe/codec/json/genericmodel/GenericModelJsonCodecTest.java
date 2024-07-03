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

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.data.MetaData;
import com.braintribe.model.meta.data.prompt.Visible;
import com.braintribe.model.processing.dataio.GenericModelInputStream;
import com.braintribe.model.processing.dataio.GenericModelOutputStream;
import com.braintribe.testing.category.KnownIssue;
import com.braintribe.utils.IOTools;


public class GenericModelJsonCodecTest {
	@SuppressWarnings("resource")
	@Test
	@Category(KnownIssue.class)
	public void testDumps() throws Exception {
		File dataFile = new File("./data/entityQueryResult.json");
		
		String encodedData = IOTools.slurp(dataFile, "UTF-8");
		GenericModelJsonStringCodec<Object> codec = new GenericModelJsonStringCodec<Object>();
		
		Object value = codec.decode(encodedData);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GenericModelOutputStream gmOut = new GenericModelOutputStream(out);

		GmEntityType entityType = GmEntityType.T.create();
		Set<MetaData> metaData = newSet();
		entityType.setMetaData(metaData);
		
		metaData.add(Visible.T.create());
		
		//gmOut.writeObject(entityType);
		gmOut.writeObject(value);
		
		byte[] data = out.toByteArray();
		
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		
		GenericModelInputStream gmIn = new GenericModelInputStream(in);
		
		Object value2 = gmIn.readObject();
		System.out.println(value2);
	}
}

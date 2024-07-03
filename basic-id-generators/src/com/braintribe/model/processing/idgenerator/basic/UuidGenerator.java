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
package com.braintribe.model.processing.idgenerator.basic;

import java.util.UUID;

import com.braintribe.model.processing.idgenerator.api.IdGeneratorContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.idgenerator.api.IdCreationException;
import com.braintribe.model.processing.idgenerator.api.IdGenerator;
import com.braintribe.utils.RandomTools;

public class UuidGenerator implements IdGenerator<String>, com.braintribe.model.generic.processing.IdGenerator {

	private UuidMode mode = UuidMode.standard;

	public void setMode(UuidMode mode) {
		this.mode = mode;
	}
	
	@Override
	public String generateId(IdGeneratorContext context) throws IdCreationException {

		switch (mode) {
		case standard : return UUID.randomUUID().toString();
		case compact : return RandomTools.getRandom32CharactersHexString(false);
		case compactWithTimestampPrefix : return RandomTools.getRandom32CharactersHexString(true);
		default: 
			throw new UnsupportedOperationException("Mode: "+mode+" not supported.");
		}
		
	}

	@Override
	public Object generateId(GenericEntity entity) throws Exception {
		return generateId((IdGeneratorContext)null);
	}
}

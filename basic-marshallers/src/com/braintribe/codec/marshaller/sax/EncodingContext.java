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
package com.braintribe.codec.marshaller.sax;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;

public class EncodingContext<T> {

	private final Map<GenericEntity, Integer> idByEntities = new HashMap<>();
	private int idSequence = 0;
	private Writer writer;
	private EntityQueueNode firstNode; 
	private EntityQueueNode lastNode;
	private int indentation;
	private int referenceMode;
	private Writer parkedWriter;
	private final Set<String> requiredTypes = new HashSet<>();
	@SuppressWarnings("unused")
	private final GmSerializationOptions options;
	private final boolean pretty;
	
	public EncodingContext(boolean pretty, Writer writer, GmSerializationOptions options) {
		this.pretty = pretty;
		this.writer = writer;
		this.options = options;
	}
	
	public StringWriter pushBuffer() {
		parkedWriter = writer;
		StringWriter stringWriter = new StringWriter();
		writer = stringWriter;
		
		return stringWriter;
	}
	
	public void registerRequiredType(String requiredType) {
		requiredTypes.add(requiredType);
	}
	
	public Set<String> getRequiredTypes() {
		return requiredTypes;
	}
	
	public void popBuffer() {
		writer = parkedWriter;
	}
	
	public EntityQueueNode getFirstNode() {
		return firstNode;
	}

	public Integer lookupId(GenericEntity entity, EntityType<?> entityType) {
		Integer id = idByEntities.get(entity);
		
		if (id == null) {
			id = idSequence++;
			idByEntities.put(entity, id);
			EntityQueueNode node = new EntityQueueNode();
			node.entityType = entityType;
			node.entity = entity;
			
			if (firstNode == null) {
				firstNode = lastNode = node;
			}
			else {
				lastNode.next = node;
				lastNode = node;
			}
		}
		
		return id;
	}
	
	public void writeIndentation() throws IOException {
		if (!this.pretty) return;
		switch (indentation) {
		case 0:
			break;
		case 1:
			writer.write(" ");
			break;
		case 2:
			writer.write("  ");
			break;
		case 3:
			writer.write("   ");
			break;
		case 4:
			writer.write("    ");
			break;
		default:
			writer.write("     ");
		break;
		}
	}
	
	public void pushIndentation() {
		indentation++;
	}
	
	public void popIndentation() {
		indentation--;
	}
	
	public void pushReferenceMode() {
		referenceMode++;
	}
	
	public void popReferenceMode() {
		referenceMode--;
	}
	
	public boolean isReferenceMode() {
		return referenceMode > 0;
	}
}

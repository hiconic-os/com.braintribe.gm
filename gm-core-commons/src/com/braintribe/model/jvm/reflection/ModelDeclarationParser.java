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
package com.braintribe.model.jvm.reflection;
// ============================================================================
// BRAINTRIBE TECHNOLOGY GMBH - www.braintribe.com
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2018 - All Rights Reserved
// It is strictly forbidden to copy, modify, distribute or use this code without written permission
// To this file the Braintribe License Agreement applies.
// ============================================================================

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.mdec.ModelDeclaration;
import com.braintribe.model.generic.reflection.GenericModelException;

public class ModelDeclarationParser {
	
	private final static XMLInputFactory inputFactory;
	private final static Logger logger = Logger.getLogger(ModelDeclarationParser.class);
	
	static {
		inputFactory = XMLInputFactory.newInstance();

		boolean debug = logger.isDebugEnabled();
		try {
			inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false); // This disables DTDs entirely for that factory
		} catch(Exception e) {
			if (debug) logger.debug("Could not set feature "+XMLInputFactory.SUPPORT_DTD+"=false", e);
		}

		try {
			inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false); // disable external entities
		} catch(Exception e) {
			if (debug) logger.debug("Could not set feature javax.xml.stream.isSupportingExternalEntities=false", e);
		}
	}
	
	
	public static ModelDeclaration parse(InputStream in) {
		return parse(in, null);
	}
	
	public static ModelDeclaration parse(InputStream in, ModelDeclarationRegistry lookup) {
		
		try {
			ModelDeclaration cpDeclaration = ModelDeclaration.T.createPlain();

			List<ModelDeclaration> dependencies = new ArrayList<>();
			Set<String> types = new HashSet<>();
			Consumer<String> consumer = null;
			XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
			try  {
				StringBuilder textBuilder = new StringBuilder();
				while(reader.hasNext()){
					reader.next();
					switch (reader.getEventType()) {
					case XMLStreamReader.START_ELEMENT:
						String tagName = reader.getLocalName();
						textBuilder.setLength(0);
						switch (tagName) {
						case "name": consumer = cpDeclaration::setName; break;
						case "groupId": consumer = cpDeclaration::setGroupId; break;
						case "artifactId": consumer = cpDeclaration::setArtifactId; break;
						case "version": consumer = cpDeclaration::setVersion; break;
						case "hash": consumer = cpDeclaration::setHash; break;
						case "globalId": consumer = cpDeclaration::setModelGlobalId; break;
						
						case "dependencies": consumer = null; break;
						case "types": consumer = null; break;
						
						case "dependency":
							consumer = s -> {
								ModelDeclaration dependency = acquireDeclaration(lookup, s);
								dependencies.add(dependency);
							};
							break;
						case "type":
							consumer = s -> {
								types.add(s);
							};
							break;
						}
						
						break;
						
					case XMLStreamReader.END_ELEMENT:
						if (consumer != null) {
							consumer.accept(textBuilder.toString());
							consumer = null;
						}
						break;
						
					case XMLStreamReader.CHARACTERS:
					case XMLStreamReader.SPACE:
						textBuilder.append(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
						break;
					default:
						break;
					}
				}
			}
			finally {
				reader.close();
			}

			ModelDeclaration declaration = acquireDeclaration(lookup, cpDeclaration.getName());
			declaration.setGroupId(cpDeclaration.getGroupId());
			declaration.setArtifactId(cpDeclaration.getArtifactId());
			declaration.setVersion(cpDeclaration.getVersion());
			declaration.setModelGlobalId(cpDeclaration.getModelGlobalId());
			declaration.setHash(cpDeclaration.getHash());
			declaration.setDependencies(dependencies);
			declaration.setTypes(types);
			
			return declaration;
		} catch (Exception e) {
			throw new GenericModelException("error while parsing model declaration xml", e);
		}
	}

	private static ModelDeclaration acquireDeclaration(ModelDeclarationRegistry lookup, String name) {
		if (lookup != null) {
			return lookup.acquireModelDeclaration(name);
		}
		else {
			ModelDeclaration declaration = ModelDeclaration.T.create();
			declaration.setName(name);
			return declaration;
		}
	}

}

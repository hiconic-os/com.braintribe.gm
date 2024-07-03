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
package com.braintribe.model.io.metamodel.render;

import java.util.Map;

import com.braintribe.logging.Logger;
import com.braintribe.model.io.metamodel.MetaModelSourceDescriptor;
import com.braintribe.model.io.metamodel.render.serializer.SourceSerializer;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumType;

/**
 * Class that manages rendering and serializing of source files for given GmMetaModel. See method
 * {@link #generateSources}.
 */
public class MetaModelSourceGenerator {

	private final SourceWriterContext context;
	private final MetaModelRenderer metaModelRenderer;
	private SourceSerializer sourceSerializer;

	private static final Logger log = Logger.getLogger(MetaModelSourceGenerator.class);

	public MetaModelSourceGenerator(SourceWriterContext context) {
		this.context = context;
		this.metaModelRenderer = new MetaModelRenderer(context);
	}

	/**
	 * Generates sources for given MetaModel and stores them in provided outputDirectory. In case it tries to create a
	 * file with name, that is already used by an existing file, this existing file is copied into a backup directory
	 * inside the outputDirectory.
	 */
	public Map<String, String> generateSources(SourceSerializer sourceSerializer) {
		this.sourceSerializer = sourceSerializer;

		generateEntityTypes();
		generateEnums();

		return sourceSerializer.getSourceMap();
	}
	
	private void generateEntityTypes() {
		context.modelOracle.getTypes().onlyDeclared().onlyEntities().<GmEntityType> asGmTypes().forEach(gmEntityType -> {
			log.info("Processing type [" + gmEntityType.getTypeSignature() + "]");
			
			try {
				MetaModelSourceDescriptor sourceDescriptor = metaModelRenderer.renderEntityType(gmEntityType);
				store(sourceDescriptor);

			} catch (Exception e) {
				log.error("Failed to render entity type " + gmEntityType.getTypeSignature(), e);
			}
		});
	}

	private void generateEnums() {
		context.modelOracle.getTypes().onlyDeclared().onlyEnums().<GmEnumType> asGmTypes().forEach(gmEnumType -> {
			try {
				MetaModelSourceDescriptor sourceDescriptor = metaModelRenderer.renderEnumType(gmEnumType);
				store(sourceDescriptor);

			} catch (Exception e) {
				log.error("Failed to render enum type " + gmEnumType.getTypeSignature(), e);
			}
		});
	}

	private void store(MetaModelSourceDescriptor sourceDescriptor) {
		if (sourceDescriptor != null) {
			sourceSerializer.writeSourceFile(sourceDescriptor);
		}
	}

}

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
package com.braintribe.codec.marshaller.yaml;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.IdentityManagementMode;
import com.braintribe.codec.marshaller.api.TypeExplicitness;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.processing.traverse.ConfigurableEntityVisiting;

public class StatefulYamlMarshaller extends AbstractStatefulYamlMarshaller {
	private static class EntityAnchoring {
		public int refCount;
		public String anchor;
		public boolean visited;
	}

	private final Map<GenericEntity, StatefulYamlMarshaller.EntityAnchoring> anchors = new IdentityHashMap<>();

	public StatefulYamlMarshaller(GmSerializationOptions options, Writer writer, Object rootValue) {
		super(options, writer, rootValue);
	}

	public void write() {
		new ConfigurableEntityVisiting(this::indexEntity).visit(rootValue);
		try {
			write(options.getInferredRootType(), BaseType.INSTANCE, rootValue);
			writer.write('\n');
		} catch (IOException e) {
			throw new UncheckedIOException("Error while marshalling: " + rootValue, e);
		} 
	}

	private boolean indexEntity(EntityType<?> entityType, GenericEntity entity) {
		StatefulYamlMarshaller.EntityAnchoring anchoring = anchors.computeIfAbsent(entity, e -> new EntityAnchoring());
		return ++anchoring.refCount == 1;
	}

	@Override
	protected void writeEntity(GenericModelType inferredType, GenericEntity entity, boolean isComplexPropertyValue) throws IOException {
		StatefulYamlMarshaller.EntityAnchoring anchoring = anchors.get(entity);

		int tokenCounter = isComplexPropertyValue ? 1 : 0;

		if (anchoring.visited && identityManagementMode != IdentityManagementMode.off) {
			writeSpacer(tokenCounter++, writer);
			writer.write('*');
			writer.write(anchoring.anchor);

			return;
		}

		if (entityVisitor != null && !anchoring.visited)
			entityVisitor.accept(entity);

		anchoring.visited = true;
		boolean entityIntroductionWritten = false;
		
		if (anchoring.refCount > 1 && identityManagementMode != IdentityManagementMode.off) {

			String anchor = String.valueOf(anchorSequence++);
			anchoring.anchor = anchor;
			writeSpacer(tokenCounter++, writer);
			writer.write('&');
			writer.write(anchor);
			entityIntroductionWritten = true;
		}

		EntityType<?> entityType = entity.entityType();

		if (typeExplicitness != TypeExplicitness.never && (typeExplicitness != TypeExplicitness.polymorphic || entity.entityType() != inferredType)) {
			writeSpacer(tokenCounter++, writer);
			writer.write('!');
			writer.write(entityType.getTypeSignature());
			entityIntroductionWritten = true;
		}

		boolean propertiesWritten = false;

		for (Property property : properties(entityType)) {

			boolean startWithNewline = isComplexPropertyValue || propertiesWritten || entityIntroductionWritten;
			
			Object directValue = property.getDirectUnsafe(entity);
			
			if (VdHolder.isVdHolder(directValue)) {
				VdHolder vdHolder = (VdHolder)directValue;
				if (vdHolder.isAbsenceInformation) {
					if (options.writeAbsenceInformation()) {
						AbsenceInformation absenceInformation = (AbsenceInformation)vdHolder.vd;
	
						if (startWithNewline) {
							writer.write('\n');
							indent.write(writer);
						}
	
						if (absenceInformation == GMF.absenceInformation()) {
							writer.write(property.getName());
							writer.write("?: absent");
						} else {
							writer.write(property.getName());
							writer.write(": ");
							write(AbsenceInformation.T, AbsenceInformation.T, absenceInformation, true);
						}
						propertiesWritten = true;
					}
				}
				else if (placeholderSupport) {
					if (startWithNewline) {
						writer.write('\n');
						indent.write(writer);
					}

					writer.write(property.getName());
					writer.write(": ");
					writePlaceholder(vdHolder.vd);
					propertiesWritten = true;
				}
			} else {
				Object value = property.get(entity);
				
				if (property.isEmptyValue(value) && !options.writeEmptyProperties())
					continue;

				if (startWithNewline) {
					writer.write('\n');
					indent.write(writer);
				}
				propertiesWritten = true;
				writer.write(property.getName());
				writer.write(':');

				GenericModelType type = property.getType();
				indent.pushIndent();
				write(type, type, value, true);
				indent.popIndent();
			}
		}

		if (!propertiesWritten) {
			writeSpacer(tokenCounter++, writer);
			writer.write("{}");
		}
	}
}
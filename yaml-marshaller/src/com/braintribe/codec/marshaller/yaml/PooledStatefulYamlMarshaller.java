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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;

public class PooledStatefulYamlMarshaller extends AbstractStatefulYamlMarshaller {
	private final Map<GenericEntity, String> anchors = new IdentityHashMap<>();
	private final Queue<PooledEntity> poolQueue = new LinkedBlockingQueue<>();
	
	private static class PooledEntity {
		GenericEntity entity;
		String anchor;
		public PooledEntity(GenericEntity entity, String anchor) {
			super();
			this.entity = entity;
			this.anchor = anchor;
		}
	}

	public PooledStatefulYamlMarshaller(GmSerializationOptions options, Writer writer, Object rootValue) {
		super(options, writer, rootValue);
	}

	public void write() {
		try {
			
			writer.write("!pooled\n\n");
			writer.write("value:");
			write(options.getInferredRootType(), BaseType.INSTANCE, rootValue, true);
			
			if (poolQueue.isEmpty()) {
				writer.write('\n');
				return;
			}
			
			writer.write("\n\npool:\n");
			PooledEntity pooledEntity = null;
			while ((pooledEntity = poolQueue.poll()) != null) {
				writePoolEntity(pooledEntity);
			}
			
		} catch (IOException e) {
			throw new UncheckedIOException("Error while marshalling: " + rootValue, e);
		}
	}
	
	private String anchorOf(GenericEntity entity) {
		return anchors.computeIfAbsent(entity, this::newAnchor);
	}
	
	private String newAnchor(GenericEntity e) {
		String anchor = String.valueOf(anchorSequence++);
		poolQueue.offer(new PooledEntity(e, anchor));
		return anchor;
	}
	
	@Override
	protected void writeEntity(GenericModelType inferredType, GenericEntity entity, boolean isComplexPropertyValue) throws IOException {
		String anchor = anchorOf(entity);
		
		int tokenCounter = isComplexPropertyValue ? 1 : 0;

		writeSpacer(tokenCounter++, writer);
		writer.write('*');
		writer.write(anchor);
		return;
	}

	private void writePoolEntity(PooledEntity pooledEntity) throws IOException {
		String anchor = pooledEntity.anchor;
		GenericEntity entity = pooledEntity.entity;

		writer.write("\n- &");
		writer.write(anchor);

		EntityType<?> entityType = entity.entityType();

		writer.write(" !");
		writer.write(entityType.getTypeSignature());

		boolean propertiesWritten = false;

		for (Property property : properties(entityType)) {

			Object value = property.get(entity);

			if (VdHolder.isVdHolder(value)) {
				VdHolder vdHolder = (VdHolder)value;
				if (vdHolder.isAbsenceInformation) {
					if (options.writeAbsenceInformation()) {
						AbsenceInformation absenceInformation = (AbsenceInformation)value;

						writer.write("\n ");
						indent.write(writer);

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
					writer.write('\n');
					indent.write(writer);
					writer.write(property.getName());
					writer.write(": ");
					writePlaceholder(vdHolder.vd);
					propertiesWritten = true;
				}
			}
			else {
				if (property.isEmptyValue(value) && !options.writeEmptyProperties())
					continue;

				writer.write("\n ");
				indent.write(writer);

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
			writer.write(" {}\n");
		}
		else {
			writer.write('\n');
		}
	}
}
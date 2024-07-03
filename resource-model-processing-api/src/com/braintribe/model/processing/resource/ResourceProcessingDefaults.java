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
package com.braintribe.model.processing.resource;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.specification.ResourceSpecification;

public interface ResourceProcessingDefaults {

	default void transferProperties(Resource source, Resource resource) {
		transferProperties(source, resource, EntityType::create);
	}
	default void transferProperties(Resource source, Resource resource, Function<EntityType<?>,GenericEntity> entityFactory) {

		if (source.getMd5() != null) {
			resource.setMd5(source.getMd5());
		}

		if (source.getName() != null) {
			resource.setName(source.getName());
		}

		if (source.getFileSize() != null) {
			resource.setFileSize(source.getFileSize());
		}

		if (source.getMimeType() != null) {
			resource.setMimeType(source.getMimeType());
		}

		if (source.getCreated() != null) {
			resource.setCreated(source.getCreated());
		}

		if (source.getCreator() != null) {
			resource.setCreator(source.getCreator());
		}

		ResourceSpecification sourceSpec = source.getSpecification();
		if (sourceSpec != null) {
			
			ResourceSpecification clonedSpec = sourceSpec.entityType().clone(new StandardCloningContext() {
				@Override
				public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity instanceToBeCloned) {
					return entityFactory.apply(entityType);
				}
				@Override
				public boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property,
						GenericEntity instanceToBeCloned, GenericEntity clonedInstance, AbsenceInformation sourceAbsenceInformation) {
					if (property.isIdentifier() || property.isPartition() || property.isGlobalId()) {
						return false;
					}
					return super.canTransferPropertyValue(entityType, property, instanceToBeCloned, clonedInstance, sourceAbsenceInformation);
				}
			}, sourceSpec, StrategyOnCriterionMatch.skip);
			resource.setSpecification(clonedSpec);
		}

		if (source.getTags() != null && !source.getTags().isEmpty()) {
			if (resource.getTags() == null) {
				Set<String> tags = new HashSet<>();
				tags.addAll(source.getTags());
				resource.setTags(tags);
			} else {
				resource.getTags().addAll(source.getTags());
			}
		}

	}

}

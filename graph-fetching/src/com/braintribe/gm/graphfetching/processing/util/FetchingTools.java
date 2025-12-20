package com.braintribe.gm.graphfetching.processing.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public interface FetchingTools {

	static <E extends GenericEntity> E cloneDetachment(E entity) {
		EntityType<E> entityType = entity.entityType();
		E clonedEntity = entityType.create();
		for (Property property: entityType.getProperties()) {
			if (property.getType().isScalar() || property.isIdentifier()) {
				property.set(clonedEntity, property.get(entity));
			}
			else {
				property.setAbsenceInformation(clonedEntity, GMF.absenceInformation());
			}
		}
		
		return clonedEntity;
	}
	
	static void absentifyNonScalarProperties(GenericEntity entity) {
		EntityType<?> entityType = entity.entityType();
		for (Property property: entityType.getProperties()) {
			if (property.getType().isScalar() || property.isIdentifier())
				continue;
			
			property.setAbsenceInformation(entity, GMF.absenceInformation());
		}
	}
	
	static CompletableFuture<Void> futureOf(Collection<? extends CompletableFuture<?>> futures) {
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
	}

	static Map<Object, GenericEntity> filterSpecificType(EntityType<?> baseType, EntityType<?> specificType, Map<Object, GenericEntity> entities) {
		if (specificType == baseType)
			return entities;
		
		Map<Object, GenericEntity> filteredEntities = new HashMap<Object, GenericEntity>();

		for (Map.Entry<Object, GenericEntity> entry: entities.entrySet()) {
			GenericEntity entity = entry.getValue();
			
			if (specificType.isInstance(entity))
				filteredEntities.put(entry.getKey(), entity);
		}
		
		return filteredEntities;
	}

}

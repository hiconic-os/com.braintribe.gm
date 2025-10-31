package com.braintribe.gm.graphfetching.processing.util;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;

public interface FetchingTools {

	public static <E extends GenericEntity> E cloneDetachment(E entity) {
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
}

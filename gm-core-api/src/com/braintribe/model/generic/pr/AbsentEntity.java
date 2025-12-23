package com.braintribe.model.generic.pr;

import com.braintribe.model.generic.annotation.ForwardDeclaration;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.EntityReference;

@ForwardDeclaration("com.braintribe.gm:absence-information-model")
@SuppressWarnings("unusable-by-js")
public interface AbsentEntity extends AbsenceInformation, EntityReference {
	
	final EntityType<AbsentEntity> T = EntityTypes.T(AbsentEntity.class);

	static AbsentEntity create(String typeSignature, Object id) {
		AbsentEntity absentEntity = AbsentEntity.T.create();
		absentEntity.setTypeSignature(typeSignature);
		absentEntity.setRefId(id);
		return absentEntity;
	}
	
	static AbsentEntity create(EntityType<?> entityType, Object id) {
		return create(entityType.getTypeSignature(), id);
	}
}
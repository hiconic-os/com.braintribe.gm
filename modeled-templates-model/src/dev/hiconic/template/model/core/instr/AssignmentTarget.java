package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

@Abstract
public interface AssignmentTarget extends GenericEntity {
	EntityType<AssignmentTarget> T = EntityTypes.T(AssignmentTarget.class);
	PropertyLiteral nullable = PropertyLiteral.of(T, "nullable");
	String getTypeSignature();
	void setTypeSignature(String typeSignature);
	boolean getNullable();
	void setNullable(boolean nullable);
}

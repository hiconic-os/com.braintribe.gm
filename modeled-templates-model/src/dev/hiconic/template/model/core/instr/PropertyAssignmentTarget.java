package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface PropertyAssignmentTarget extends AssignmentTarget {
	EntityType<PropertyAssignmentTarget> T = EntityTypes.T(PropertyAssignmentTarget.class);

	PropertyLiteral path = PropertyLiteral.of(T, "path");
	dev.hiconic.template.model.core.vd.TemplatePropertyPath getPath();
	void setPath(dev.hiconic.template.model.core.vd.TemplatePropertyPath path);
}

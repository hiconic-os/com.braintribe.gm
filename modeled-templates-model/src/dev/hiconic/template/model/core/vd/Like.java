package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

public interface Like extends BooleanDescriptor {
	EntityType<Like> T = EntityTypes.T(Like.class);
	
	PropertyLiteral candidate = PropertyLiteral.of(T, "candidate");
	PropertyLiteral pattern = PropertyLiteral.of(T, "pattern");
	
	String getCandidate();
	void setCandidate(String candidate);
	
	String getPattern();
	void setPattern(String pattern);
}

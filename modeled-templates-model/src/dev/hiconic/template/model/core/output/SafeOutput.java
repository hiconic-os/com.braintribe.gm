package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

public interface SafeOutput extends Output {
	EntityType<SafeOutput> T = EntityTypes.T(SafeOutput.class);
	
	PropertyLiteral text = PropertyLiteral.of(T, "text");
	
	String getText();
	void setText(String text);
}

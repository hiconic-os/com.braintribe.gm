package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.output.Transformer;

public interface TransformValue extends ExplicitlyTypedDescriptor {
	EntityType<TransformValue> T = EntityTypes.T(TransformValue.class);

	PropertyLiteral input = PropertyLiteral.of(T, "input");
	PropertyLiteral transformer = PropertyLiteral.of(T, "transformer");
	PropertyLiteral inputTypeSignature = PropertyLiteral.of(T, "inputTypeSignature");

	Object getInput();
	void setInput(Object input);

	Transformer getTransformer();
	void setTransformer(Transformer transformer);

	String getInputTypeSignature();
	void setInputTypeSignature(String inputTypeSignature);
}

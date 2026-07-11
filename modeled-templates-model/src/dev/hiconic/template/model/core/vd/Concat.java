package dev.hiconic.template.model.core.vd;

import java.util.List;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.StringDescriptor;

@PositionalArguments("operands")
public interface Concat extends StringDescriptor {
	EntityType<Concat> T = EntityTypes.T(Concat.class);

	PropertyLiteral operands = PropertyLiteral.of(T, "operands");

	List<Object> getOperands();
	void setOperands(List<Object> operands);
}

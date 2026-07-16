package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Default extends SwitchCase {
	EntityType<Default> T = EntityTypes.T(Default.class);
}

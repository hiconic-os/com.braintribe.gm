package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface SwitchCase extends BlockClause {
	EntityType<SwitchCase> T = EntityTypes.T(SwitchCase.class);
}

package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface CssOutput extends SafeOutput {
	EntityType<CssOutput> T = EntityTypes.T(CssOutput.class);
}

package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface UrlComponentOutput extends SafeOutput {
	EntityType<UrlComponentOutput> T = EntityTypes.T(UrlComponentOutput.class);
}

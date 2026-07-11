package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface HtmlOutput extends SafeOutput {
	EntityType<HtmlOutput> T = EntityTypes.T(HtmlOutput.class);
}

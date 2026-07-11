package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface XmlOutput extends SafeOutput {
	EntityType<XmlOutput> T = EntityTypes.T(XmlOutput.class);
}

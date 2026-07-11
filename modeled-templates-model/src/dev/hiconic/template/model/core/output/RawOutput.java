package dev.hiconic.template.model.core.output;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Explicitly trusted, unescaped output. Its use should be policy controlled.
 */
public interface RawOutput extends SafeOutput {
	EntityType<RawOutput> T = EntityTypes.T(RawOutput.class);
}

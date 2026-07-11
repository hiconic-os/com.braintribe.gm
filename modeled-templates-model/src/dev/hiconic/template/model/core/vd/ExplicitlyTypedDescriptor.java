package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.type.DynamicallyTypedDescriptor;

/**
 * A descriptor whose type is determined during model completion and persisted
 * as a type signature. Once completed, consumers no longer need its expert to
 * determine the result type.
 */
@Abstract
public interface ExplicitlyTypedDescriptor extends DynamicallyTypedDescriptor {
	EntityType<ExplicitlyTypedDescriptor> T = EntityTypes.T(ExplicitlyTypedDescriptor.class);
}

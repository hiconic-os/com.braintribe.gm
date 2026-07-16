package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

public interface VdEvaluator<V extends ValueDescriptor, O> {
    Maybe<O> transform(TemplateEvaluationContext context, V vd);

	default GenericModelType expectedArgumentType(ValidationContext context, V vd, Property property) { return null; }

    /**
     * Validates and completes a descriptor after parsing. A successful
     * completion must leave all information required for type-only validation
     * on the descriptor itself.
     */
    default Reason complete(ValidationContext context, V vd) {
    	return validate(context, vd);
    }

    /**
     * Compatibility hook for validators which do not enrich their descriptor.
     * New context-dependent descriptors should implement {@link #complete}.
     */
    default Reason validate(ValidationContext context, V vd) { return null; }
}

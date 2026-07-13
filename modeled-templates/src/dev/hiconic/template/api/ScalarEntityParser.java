package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.GenericEntity;

@FunctionalInterface
public interface ScalarEntityParser<E extends GenericEntity> {
	Maybe<E> parse(String source, ValidationContext context);
}

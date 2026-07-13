package dev.hiconic.template.impl.parser;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.GenericModelType;
import dev.hiconic.template.model.parse.TextRange;

@FunctionalInterface
public interface ArgumentValueResolver {
	Maybe<ParsedValueExpression> resolve(String source, GenericModelType expectedType, TextRange range);
}

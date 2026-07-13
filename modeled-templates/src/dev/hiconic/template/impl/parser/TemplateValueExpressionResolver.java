package dev.hiconic.template.impl.parser;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.model.parse.TextRange;

public interface TemplateValueExpressionResolver {
	Maybe<ParsedValueExpression> resolveValue(String expression, TextRange range);

	default Maybe<ParsedValueExpression> resolveArgumentValue(String expression, TextRange range) {
		Maybe<ParsedValueExpression> value = resolveValue(expression, range);
		return value.isSatisfied()
				? value
				: Maybe.complete(new ParsedValueExpression(expression, SimpleTypes.TYPE_STRING));
	}

	default Maybe<ParsedValueExpression> resolveArgumentValue(String expression, GenericModelType expectedType,
			TextRange range) {
		return resolveArgumentValue(expression, range);
	}
}

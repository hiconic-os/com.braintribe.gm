package dev.hiconic.template.impl.parser;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.api.TemplateParserResolver;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.parse.TextRange;

/**
 * Decorates vocabulary resolution with the standard expert-backed completion
 * and validation pass.
 */
public class CompletingTemplateParserResolver implements TemplateParserResolver {
	private final TemplateParserResolver delegate;
	private final ExpertModelCompleter completer;

	public CompletingTemplateParserResolver(TemplateParserResolver delegate, ExpertModelCompleter completer) {
		this.delegate = delegate;
		this.completer = completer;
	}

	@Override
	public void beginParse() {
		delegate.beginParse();
	}

	@Override
	public void endParse() {
		delegate.endParse();
	}

	@Override
	public Maybe<OutputNode> resolveOutput(String expression, TextRange range) {
		return delegate.resolveOutput(expression, range);
	}

	@Override
	public Maybe<? extends TemplateNode> resolveDirective(char sigil, String invocation, boolean blockFree, TextRange range) {
		return delegate.resolveDirective(sigil, invocation, blockFree, range);
	}

	@Override
	public Reason enterBlock(TemplateNode owner, String blockProperty, TextRange range) {
		return delegate.enterBlock(owner, blockProperty, range);
	}

	@Override
	public void exitBlock(TemplateNode owner, String blockProperty) {
		delegate.exitBlock(owner, blockProperty);
	}

	@Override
	public Reason completeAndValidate(ValidationContext context, TemplateNode node, TextRange range) {
		Reason reason = delegate.completeAndValidate(context, node, range);
		return reason != null ? reason : completer.completeAndValidate(context, node);
	}
}

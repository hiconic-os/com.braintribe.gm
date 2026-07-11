package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;

import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.parse.TextRange;

/**
 * Resolves model vocabulary while the parser owns textual grammar and block
 * structure.
 */
public interface TemplateParserResolver {
	Maybe<OutputNode> resolveOutput(String expression, TextRange range);

	/**
	 * Resolves '%' instructions and '#' declarations. The sigil is passed so
	 * both namespaces can use the same textual name.
	 */
	Maybe<? extends TemplateNode> resolveDirective(char sigil, String invocation, boolean blockFree, TextRange range);

	/**
	 * Opens the validation scope for a block before its content is parsed.
	 * Loop-variable types and similar symbols belong here.
	 */
	default Reason enterBlock(TemplateNode owner, String blockProperty, TextRange range) {
		return null;
	}

	default void exitBlock(TemplateNode owner, String blockProperty) {
	}

	/**
	 * Completes context-dependent descriptors and validates the fully wired
	 * node. Returns {@code null} on success.
	 */
	Reason completeAndValidate(ValidationContext context, TemplateNode node, TextRange range);
}

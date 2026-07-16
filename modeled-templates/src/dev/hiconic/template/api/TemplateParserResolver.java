package dev.hiconic.template.api;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.TemplateNode;
import dev.hiconic.template.model.parse.TextRange;

/**
 * Resolves model vocabulary while the parser owns textual grammar and block
 * structure.
 */
public interface TemplateParserResolver {
	/** Starts a parser run; implementations may reset per-document state here. */
	default void beginParse() {
	}

	/** Ends a parser run and releases per-document state. */
	default void endParse() {
	}

	Maybe<OutputNode> resolveOutput(String expression, TextRange range);

	/**
	 * Resolves '%' instructions and '#' declarations. The sigil is passed so
	 * both namespaces can use the same textual name.
	 */
	Maybe<? extends TemplateNode> resolveDirective(char sigil, String invocation, boolean blockFree, TextRange range);

	default java.util.Set<String> clauseMarkers(GenericModelType expectedClauseType) {
		return java.util.Set.of();
	}

	default Maybe<? extends GenericEntity> resolveClause(GenericModelType expectedClauseType, String marker,
			String invocation, TextRange range) {
		return Maybe.empty(com.braintribe.gm.model.reason.essential.ParseError.create(
				"Clauses are not supported by this resolver"));
	}

	/**
	 * Registers a declaration signature before the containing sequence is parsed.
	 * This makes declarations lexical rather than order-dependent and permits
	 * recursive as well as mutually recursive declared instructions.
	 */
	default Reason predeclareDirective(char sigil, String invocation, TextRange range) {
		return null;
	}

	/**
	 * Opens the validation scope for a block before its content is parsed.
	 * Loop-variable types and similar symbols belong here.
	 */
	default Reason enterBlock(TemplateNode owner, String blockProperty, TextRange range) {
		return null;
	}
	default Reason completeScope(TemplateNode owner, TextRange range) { return null; }

	default void exitBlock(TemplateNode owner, String blockProperty) {
	}

	/**
	 * Completes context-dependent descriptors and validates the fully wired
	 * node. Returns {@code null} on success.
	 */
	Reason completeAndValidate(ValidationContext context, TemplateNode node, TextRange range);
}

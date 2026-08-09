package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.TemplateNode;

/** A model-backed directive that may occur in the {@code %(Type ...)} form. */
@Abstract
public interface DirectiveNode extends TemplateNode {
	EntityType<DirectiveNode> T = EntityTypes.T(DirectiveNode.class);
	PropertyLiteral whitespace = PropertyLiteral.of(T, "whitespace");
	PropertyLiteral promote = PropertyLiteral.of(T, "promote");

	WhitespacePolicy getWhitespace();
	void setWhitespace(WhitespacePolicy whitespace);

	/**
	 * Promotes this directive's structural enclosing up the containment hierarchy to a coarser,
	 * WYSIWYG-inaccessible level, so the block is cloned/kept as that unit (e.g. a whole table row
	 * per {@code for-each} iteration). {@code null} = no promotion (inline). The value is a concrete
	 * {@link StructuralScope} (e.g. a document-domain {@code Row}/{@code Cell}); a text sink ignores
	 * it. Modeled as an extensible entity hierarchy rather than an enum, so scope kinds can carry
	 * their own fine-tuning properties later.
	 */
	StructuralScope getPromote();
	void setPromote(StructuralScope promote);
}

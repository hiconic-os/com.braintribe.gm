package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

/**
 * The structural container a block's boundary snaps to when the sink is structured (a document).
 *
 * <p>This is the coarse end of the same boundary-policy axis as {@link WhitespaceAction} — where
 * {@code trimLine} snaps a block's boundary to the enclosing <em>line</em>, a {@code StructuralScope}
 * snaps it to an enclosing structural node so the block is cloned as that unit (e.g. a whole table
 * row per {@code for-each} iteration). The concept lives in the base; the concrete vocabulary
 * (row/cell/…) is a document-domain refinement supplied by a satellite model, and the mapping to a
 * vendor node happens only in the sink. A {@code null} snap means the default: inline (no structural
 * snap), i.e. the block repeats its reading-order content in place.
 */
@Abstract
public interface StructuralScope extends GenericEntity {
	EntityType<StructuralScope> T = EntityTypes.T(StructuralScope.class);

	PropertyLiteral scaffold = PropertyLiteral.of(T, "scaffold");

	/**
	 * When {@code true}, the container(s) hosting this loop's control markers ({@code for-each} /
	 * {@code end}) are treated as dedicated <em>scaffold</em> — structure that carries the control logic
	 * during building and is removed from the output (the materialized stand-in for the inter-element
	 * space WYSIWYG lacks). Explicit and opt-in: default {@code false} (inline — markers vanish but their
	 * host container stays). A text sink ignores it.
	 */
	boolean getScaffold();
	void setScaffold(boolean scaffold);
}

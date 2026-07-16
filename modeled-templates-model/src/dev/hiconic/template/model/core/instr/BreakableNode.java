package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Capability marker for block nodes that consume a nested {@link Break}. */
@Abstract
public interface BreakableNode extends BlockNode {
	EntityType<BreakableNode> T = EntityTypes.T(BreakableNode.class);
}

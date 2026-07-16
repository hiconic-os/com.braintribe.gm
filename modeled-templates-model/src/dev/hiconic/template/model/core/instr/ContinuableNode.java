package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Capability marker for block nodes that consume a nested {@link Continue}. */
@Abstract
public interface ContinuableNode extends BlockNode {
	EntityType<ContinuableNode> T = EntityTypes.T(ContinuableNode.class);
}

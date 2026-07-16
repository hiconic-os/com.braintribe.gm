package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** Block instruction whose primary block may only contain structural whitespace/comments. */
public interface ClauseOnlyBlockNode extends BlockInstructionNode {
	EntityType<ClauseOnlyBlockNode> T = EntityTypes.T(ClauseOnlyBlockNode.class);
}

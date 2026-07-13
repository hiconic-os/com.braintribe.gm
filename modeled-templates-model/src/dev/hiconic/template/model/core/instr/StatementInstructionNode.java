package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/** An instruction whose evaluation performs a side effect but renders no output. */
@Abstract
public interface StatementInstructionNode extends InstructionNode, SilentNode {
	EntityType<StatementInstructionNode> T = EntityTypes.T(StatementInstructionNode.class);
}

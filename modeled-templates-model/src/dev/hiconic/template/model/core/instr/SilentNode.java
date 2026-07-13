package dev.hiconic.template.model.core.instr;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import dev.hiconic.template.model.core.TemplateNode;

/** A node that has no output at its syntactic occurrence. */
@Abstract
public interface SilentNode extends TemplateNode {
	EntityType<SilentNode> T = EntityTypes.T(SilentNode.class);
}

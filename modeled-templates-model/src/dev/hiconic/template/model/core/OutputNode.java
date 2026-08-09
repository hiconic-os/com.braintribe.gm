package dev.hiconic.template.model.core;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;

import dev.hiconic.template.model.core.output.Output;

public interface OutputNode extends TemplateNode {
	EntityType<OutputNode> T = EntityTypes.T(OutputNode.class);

	PropertyLiteral output = PropertyLiteral.of(T, "output");

	/**
	 * The value this node emits. Widened from {@code SafeOutput} to the common {@link Output}
	 * supertype so a node can carry either the text-capable {@code SafeOutput} branch (understood
	 * by every sink) or a sink-specific derivative. Which concrete types are admissible is decided
	 * per sink at validation time (see {@code ValidationContext.supportedOutputType()}).
	 */
	Output getOutput();
	void setOutput(Output output);
}

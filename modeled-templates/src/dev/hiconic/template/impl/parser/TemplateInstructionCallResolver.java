package dev.hiconic.template.impl.parser;

import java.util.List;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;

import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.instr.InvokeInstruction;

/**
 * Resolves only template-local declared instructions. Statically modeled
 * InstructionNode types are instantiated and bound directly by the regular
 * model resolver and never pass through this class.
 */
public class TemplateInstructionCallResolver {
	private final TemplateDeclarationScope declarations;
	private final RuntimeInstructionNormalizer normalizer;

	public TemplateInstructionCallResolver(TemplateDeclarationScope declarations,
			RuntimeInstructionNormalizer normalizer) {
		this.declarations = declarations;
		this.normalizer = normalizer;
	}

	public Maybe<InvokeInstruction> resolve(String name, List<ParsedArgument> arguments) {
		Maybe<DeclareInstruction> declaration = declarations.resolve(name);
		return declaration.isSatisfied()
				? normalizer.normalize(declaration.get(), arguments)
				: Maybe.empty(ParseError.create("Unknown declared instruction: " + name));
	}
}

package dev.hiconic.template.impl.parser;

public class ReflectedInstructionNormalizer extends ReflectedEntityNormalizer {
	public ReflectedInstructionNormalizer() {
	}

	public ReflectedInstructionNormalizer(ArgumentValueResolver valueResolver) {
		super(valueResolver);
	}

	public ReflectedInstructionNormalizer(ArgumentValueResolver valueResolver, ArgumentTypeRecorder typeRecorder) {
		super(valueResolver, typeRecorder);
	}

	public ReflectedInstructionNormalizer(ArgumentValueResolver valueResolver, ArgumentTypeRecorder typeRecorder,
			ExpectedTypeResolver expectedTypeResolver) {
		super(valueResolver, typeRecorder, expectedTypeResolver);
	}
}

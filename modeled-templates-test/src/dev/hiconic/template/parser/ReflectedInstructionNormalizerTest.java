package dev.hiconic.template.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.SimpleTypes;

import dev.hiconic.template.impl.parser.ParsedArgument;
import dev.hiconic.template.impl.parser.ReflectedInstructionNormalizer;
import dev.hiconic.template.test.model.TestBlockInstruction;

public class ReflectedInstructionNormalizerTest {
	private final ReflectedInstructionNormalizer normalizer = new ReflectedInstructionNormalizer();

	@Test
	public void createsConcreteInstructionTypeDirectly() {
		Maybe<TestBlockInstruction> maybe = normalizer.normalize(TestBlockInstruction.T,
				List.of(new ParsedArgument(null, true, SimpleTypes.TYPE_BOOLEAN)));

		assertTrue(maybe.isSatisfied());
		assertTrue(maybe.get() instanceof TestBlockInstruction);
		assertTrue(maybe.get().getCondition());
	}

	@Test
	public void validatesRealReflectedProperties() {
		Maybe<TestBlockInstruction> maybe = normalizer.normalize(TestBlockInstruction.T,
				List.of(new ParsedArgument("condition", "wrong", SimpleTypes.TYPE_STRING)));

		assertFalse(maybe.isSatisfied());
	}
}

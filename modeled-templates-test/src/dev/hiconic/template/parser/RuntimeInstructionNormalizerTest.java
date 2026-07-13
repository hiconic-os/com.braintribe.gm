package dev.hiconic.template.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SimpleTypes;

import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.parser.ParsedArgument;
import dev.hiconic.template.impl.parser.RuntimeInstructionNormalizer;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.RuntimePropertySpecification;
import dev.hiconic.template.model.core.decl.RuntimeArguments;
import dev.hiconic.template.model.core.decl.RuntimeTypeSpecification;
import dev.hiconic.template.model.core.instr.InvokeInstruction;

public class RuntimeInstructionNormalizerTest {
	private static final ValidationContext TYPES = new ValidationContext() {
		@Override
		public GenericModelType getType(GenericEntity entity, Property property) {
			return property.getType();
		}
	};

	@Test
	public void createsTransportableTypedArgumentEnvelope() {
		DeclareInstruction declaration = declaration();
		RuntimeInstructionNormalizer normalizer = new RuntimeInstructionNormalizer(TYPES);

		Maybe<InvokeInstruction> maybe = normalizer.normalize(declaration, List.of(
				new ParsedArgument(null, "Ada", SimpleTypes.TYPE_STRING),
				new ParsedArgument("count", 3, SimpleTypes.TYPE_INTEGER)));

		assertTrue(maybe.isSatisfied());
		InvokeInstruction invocation = maybe.get();
		RuntimeArguments arguments = (RuntimeArguments) invocation.getArguments();
		assertSame(declaration, invocation.getDeclaration());
		assertSame(declaration.getArgumentType(), arguments.getTypeSpecification());
		assertEquals("name", arguments.getValues().get(0).getSpecification().getName());
		assertEquals("Ada", arguments.getValues().get(0).getValue());
		assertEquals(3, arguments.getValues().get(1).getValue());
	}

	@Test
	public void rejectsWrongAndMissingArgumentTypes() {
		RuntimeInstructionNormalizer normalizer = new RuntimeInstructionNormalizer(TYPES);

		assertTrue(normalizer.normalize(declaration(), List.of(
				new ParsedArgument("name", 42, SimpleTypes.TYPE_INTEGER),
				new ParsedArgument("count", 3, SimpleTypes.TYPE_INTEGER))).isUnsatisfied());
		assertTrue(normalizer.normalize(declaration(), List.of(
				new ParsedArgument("name", "Ada", SimpleTypes.TYPE_STRING))).isUnsatisfied());
	}

	private static DeclareInstruction declaration() {
		RuntimePropertySpecification name = property("name", SimpleTypes.TYPE_STRING, 0);
		RuntimePropertySpecification count = property("count", SimpleTypes.TYPE_INTEGER, 1);
		RuntimeTypeSpecification type = RuntimeTypeSpecification.T.create();
		type.setName("RenderArguments");
		type.setProperties(new ArrayList<>(List.of(name, count)));

		DeclareInstruction declaration = DeclareInstruction.T.create();
		declaration.setName(dev.hiconic.template.impl.parser.DefinitionTools.symbol("render"));
		declaration.setArgumentType(type);
		return declaration;
	}

	private static RuntimePropertySpecification property(String name, GenericModelType type, int position) {
		RuntimePropertySpecification property = RuntimePropertySpecification.T.create();
		property.setName(name);
		property.setTypeSignature(type.getTypeSignature());
		property.setPositionalIndex(position);
		property.setRequired(true);
		property.setMetaData(new ArrayList<>());
		return property;
	}
}

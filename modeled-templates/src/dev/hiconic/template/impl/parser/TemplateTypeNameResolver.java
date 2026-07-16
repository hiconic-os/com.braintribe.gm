package dev.hiconic.template.impl.parser;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.processing.meta.cmd.CmdResolver;

import dev.hiconic.template.model.core.instr.InstructionNode;
import dev.hiconic.template.model.core.decl.DeclarationNode;
import dev.hiconic.template.model.core.instr.BlockClause;

public class TemplateTypeNameResolver {
	public enum Usage {
		ENTITY,
		VALUE_DESCRIPTOR,
		INSTRUCTION,
		CLAUSE
	}

	private final CmdResolver cmdResolver;
	private final Function<EntityType<?>, Collection<String>> aliases;

	public TemplateTypeNameResolver(CmdResolver cmdResolver) {
		this(cmdResolver, type -> null);
	}

	/**
	 * A {@code null} alias result enables the derived default alias. A non-null
	 * collection is authoritative; an empty collection hides all aliases.
	 */
	public TemplateTypeNameResolver(CmdResolver cmdResolver,
			Function<EntityType<?>, Collection<String>> aliases) {
		this.cmdResolver = cmdResolver;
		this.aliases = aliases;
	}

	public Maybe<EntityType<?>> resolve(String name, Usage usage) {
		Maybe<EntityType<?>> explicitAlias = resolveExplicitAlias(name, usage);
		if (explicitAlias.isSatisfied())
			return explicitAlias;
		if (isLowerKebabCase(name))
			return resolveFunctionalAlias(name, usage);
		return resolveModelType(name, usage);
	}

	private Maybe<EntityType<?>> resolveExplicitAlias(String name, Usage usage) {
		List<EntityType<?>> matches = cmdResolver.getModelOracle().getTypes().onlyEntities().<EntityType<?>>asTypes()
				.filter(type -> accepts(type, usage))
				.filter(type -> hasConfiguredAlias(type, name))
				.toList();
		return unique(name, matches);
	}

	private boolean hasConfiguredAlias(EntityType<?> type, String name) {
		if (hasExplicitAlias(type, name)) return true;
		return cmdResolver.getMetaData().lenient(true).entityType(type)
				.meta(com.braintribe.model.meta.data.mapping.Alias.T).list().stream()
				.anyMatch(alias -> name.equals(alias.getName()));
	}

	public static boolean hasExplicitAlias(EntityType<?> type, String name) {
		for (Alias alias : type.getJavaType().getAnnotationsByType(Alias.class))
			if (name.equals(alias.value()))
				return true;
		return false;
	}

	private Maybe<EntityType<?>> resolveModelType(String name, Usage usage) {
		if (name.indexOf('.') >= 0) {
			GmCustomType exact = cmdResolver.getModelOracle().findGmType(name);
			if (exact instanceof GmEntityType entityType)
				return accepted(entityType.reflectionType(), usage, name);
		}

		String simpleName = name.substring(name.lastIndexOf('.') + 1);
		List<EntityType<?>> matches = new ArrayList<>();
		List<GmCustomType> candidates =
				cmdResolver.getModelOracle().findGmTypeBySimpleName(simpleName);
		for (GmCustomType candidate : candidates) {
			if (!(candidate instanceof GmEntityType entityType))
				continue;
			if (!name.equals(simpleName) && !entityType.getTypeSignature().equals(name)
					&& !entityType.getTypeSignature().endsWith("." + name))
				continue;
			EntityType<?> reflectionType = entityType.reflectionType();
			if (accepts(reflectionType, usage))
				matches.add(reflectionType);
		}
		return unique(name, matches);
	}

	private Maybe<EntityType<?>> resolveFunctionalAlias(String name, Usage usage) {
		if (usage == Usage.ENTITY)
			return error("Functional alias '" + name + "' is not valid for an ordinary entity payload");

		List<EntityType<?>> matches = cmdResolver.getModelOracle().getTypes().onlyEntities().<EntityType<?>>asTypes()
				.filter(type -> accepts(type, usage))
				.filter(type -> functionalAliases(type).contains(name))
				.toList();
		return unique(name, matches);
	}

	private Collection<String> functionalAliases(EntityType<?> type) {
		Collection<String> configured = aliases.apply(type);
		return configured != null ? configured : List.of(toLowerKebabCase(type.getShortName()));
	}

	public List<String> aliasesForAssignableClauses(EntityType<?> expectedType) {
		List<String> result = new ArrayList<>();
		cmdResolver.getModelOracle().getTypes().onlyEntities().<EntityType<?>>asTypes()
				.filter(type -> accepts(type, Usage.CLAUSE))
				.filter(type -> expectedType.isAssignableFrom(type))
				.forEach(type -> {
					for (String alias : functionalAliases(type))
						if (!result.contains(alias))
							result.add(alias);
					for (Alias alias : type.getJavaType().getAnnotationsByType(Alias.class))
						if (!result.contains(alias.value()))
							result.add(alias.value());
				});
		return result;
	}

	private Maybe<EntityType<?>> accepted(EntityType<?> type, Usage usage, String name) {
		return accepts(type, usage)
				? Maybe.complete(type)
				: error("Type '" + name + "' is not a " + usageName(usage));
	}

	private boolean accepts(EntityType<?> type, Usage usage) {
		if (type.isAbstract())
			return false;
		return switch (usage) {
			case ENTITY -> true;
			case VALUE_DESCRIPTOR -> ValueDescriptor.T.isAssignableFrom(type);
			case INSTRUCTION -> dev.hiconic.template.model.core.instr.DirectiveNode.T.isAssignableFrom(type);
			case CLAUSE -> BlockClause.T.isAssignableFrom(type);
		};
	}

	private Maybe<EntityType<?>> unique(String name, List<EntityType<?>> matches) {
		if (matches.isEmpty())
			return error("Unknown template type or alias: " + name);
		if (matches.size() > 1)
			return error("Ambiguous template type or alias '" + name + "': "
					+ matches.stream().map(GenericModelType::getTypeSignature).sorted().toList());
		return Maybe.complete(matches.get(0));
	}

	private Maybe<EntityType<?>> error(String message) {
		return Maybe.empty(ParseError.create(message));
	}

	private String usageName(Usage usage) {
		return switch (usage) {
			case ENTITY -> "model entity";
			case VALUE_DESCRIPTOR -> "value descriptor";
			case INSTRUCTION -> "template instruction";
			case CLAUSE -> "template block clause";
		};
	}

	public static boolean isLowerKebabCase(String name) {
		return name.matches("[a-z][a-z0-9]*(?:-[a-z0-9]+)*");
	}

	public static String toLowerKebabCase(String shortName) {
		return shortName
				.replaceAll("([A-Z]+)([A-Z][a-z])", "$1-$2")
				.replaceAll("([a-z0-9])([A-Z])", "$1-$2")
				.toLowerCase(Locale.ROOT);
	}
}

package dev.hiconic.template.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.time.ZoneId;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.utils.lcd.Lazy;

import dev.hiconic.template.impl.ConfigurableTemplateExpertRegistry;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;

public final class TemplateFactory {
	private final Lazy<ConfigurableTemplateExpertRegistry> registry;
	private final TemplateParserOptions options;
	private final String rootVariable;
	private final CmdResolver inputCmdResolver;
	private final CmdResolver expertCmdResolver;
	private final boolean allowNoEscape;
	private final TemplateEvaluationDefaults defaults;

	TemplateFactory(List<Consumer<TemplateExpertRegistry>> configurers, TemplateParserOptions options,
			String rootVariable, CmdResolver cmdResolver, boolean allowNoEscape) {
		this(lazyRegistry(configurers), options, rootVariable, cmdResolver, cmdResolver, allowNoEscape,
				TemplateDefaults.standard());
	}

	TemplateFactory(Supplier<ConfigurableTemplateExpertRegistry> registrySupplier, TemplateParserOptions options,
			String rootVariable, CmdResolver inputCmdResolver, CmdResolver expertCmdResolver, boolean allowNoEscape,
			TemplateEvaluationDefaults defaults) {
		this.registry = new Lazy<>(registrySupplier);
		this.options = Objects.requireNonNull(options, "options");
		this.rootVariable = Objects.requireNonNull(rootVariable, "rootVariable");
		this.inputCmdResolver = inputCmdResolver;
		this.expertCmdResolver = expertCmdResolver;
		this.allowNoEscape = allowNoEscape;
		this.defaults = Objects.requireNonNull(defaults, "defaults");
	}

	public static TemplateFactoryBuilder builder() {
		return new TemplateFactoryBuilder();
	}

	public TemplateFactory configure(Consumer<TemplateExpertRegistry> configurer) {
		Objects.requireNonNull(configurer, "configurer");
		return derive(registry -> configurer.accept(registry));
	}

	public TemplateFactory options(TemplateParserOptions options) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, inputCmdResolver, expertCmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory rootVariable(String rootVariable) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, inputCmdResolver, expertCmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory cmdResolver(CmdResolver cmdResolver) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, cmdResolver, cmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory inputCmdResolver(CmdResolver inputCmdResolver) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, inputCmdResolver, expertCmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory expertCmdResolver(CmdResolver expertCmdResolver) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, inputCmdResolver, expertCmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory allowNoEscape(boolean allowNoEscape) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, inputCmdResolver, expertCmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory defaults(TemplateEvaluationDefaults defaults) {
		return new TemplateFactory(this::prototypeRegistry, options, rootVariable, inputCmdResolver, expertCmdResolver,
				allowNoEscape, defaults);
	}

	public TemplateFactory defaultLocale(Locale locale) {
		return defaults(TemplateDefaults.derive(defaults).locale(locale).build());
	}

	public TemplateFactory defaultZone(ZoneId zone) {
		return defaults(TemplateDefaults.derive(defaults).zone(zone).build());
	}

	public TemplateFactory defaultDateFormat(String datePattern) {
		return defaults(TemplateDefaults.derive(defaults).datePattern(datePattern).build());
	}

	public TemplateFactory defaultNumberFormat(String numberPattern) {
		return defaults(TemplateDefaults.derive(defaults).numberPattern(numberPattern).build());
	}

	public TemplateFactory derive(Consumer<TemplateExpertRegistry> configurer) {
		Objects.requireNonNull(configurer, "configurer");
		return new TemplateFactory(() -> {
			ConfigurableTemplateExpertRegistry derived = prototypeRegistry();
			configurer.accept(derived);
			return derived;
		}, options, rootVariable, inputCmdResolver, expertCmdResolver, allowNoEscape, defaults);
	}

	public <I extends GenericEntity> TypedTemplateFactory<I> withRoot(EntityType<I> rootType) {
		return withRootType(rootType);
	}

	public <I> TypedTemplateFactory<I> withRoot(Class<I> javaType) {
		return withRootType(typeReflection().getType(javaType));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <E extends Enum<E>> TypedTemplateFactory<E> withEnumRoot(Class<E> enumType) {
		EnumType<?> reflectedType = typeReflection().getEnumType((Class) enumType);
		return withRootType(reflectedType);
	}

	public <I> TypedTemplateFactory<I> withRootType(GenericModelType rootType) {
		return new TypedTemplateFactory<>(this, rootType);
	}

	public <E extends GenericEntity> TypedTemplateFactory<List<E>> withListRoot(EntityType<E> elementType) {
		return withListRoot((GenericModelType) elementType);
	}

	public <E> TypedTemplateFactory<List<E>> withListRoot(GenericModelType elementType) {
		return withRootType(typeReflection().getListType(elementType));
	}

	public <E> TypedTemplateFactory<List<E>> withListRoot(Class<E> elementType) {
		return withListRoot((GenericModelType) typeReflection().getType(elementType));
	}

	public <E> TypedTemplateFactory<Set<E>> withSetRoot(GenericModelType elementType) {
		return withRootType(typeReflection().getSetType(elementType));
	}

	public <E> TypedTemplateFactory<Set<E>> withSetRoot(Class<E> elementType) {
		return withSetRoot((GenericModelType) typeReflection().getType(elementType));
	}

	public <K, V> TypedTemplateFactory<Map<K, V>> withMapRoot(GenericModelType keyType, GenericModelType valueType) {
		return withRootType(typeReflection().getMapType(keyType, valueType));
	}

	public <K, V> TypedTemplateFactory<Map<K, V>> withMapRoot(Class<K> keyType, Class<V> valueType) {
		return withMapRoot((GenericModelType) typeReflection().getType(keyType),
				(GenericModelType) typeReflection().getType(valueType));
	}

	ConfigurableTemplateExpertRegistry registry() {
		return prototypeRegistry();
	}

	TemplateParserOptions options() {
		return options;
	}

	String rootVariable() {
		return rootVariable;
	}

	CmdResolver inputCmdResolver() {
		return inputCmdResolver;
	}

	CmdResolver expertCmdResolver() {
		return expertCmdResolver;
	}

	boolean allowNoEscape() {
		return allowNoEscape;
	}

	TemplateEvaluationDefaults defaults() {
		return defaults;
	}

	private static GenericModelTypeReflection typeReflection() {
		return GMF.getTypeReflection();
	}

	private ConfigurableTemplateExpertRegistry prototypeRegistry() {
		return registry.get().copy();
	}

	static Supplier<ConfigurableTemplateExpertRegistry> lazyRegistry(
			List<Consumer<TemplateExpertRegistry>> configurers) {
		List<Consumer<TemplateExpertRegistry>> immutableConfigurers = List.copyOf(configurers);
		return () -> {
			ConfigurableTemplateExpertRegistry registry = new ConfigurableTemplateExpertRegistry();
			for (Consumer<TemplateExpertRegistry> configurer : immutableConfigurers)
				configurer.accept(registry);
			return registry;
		};
	}
}

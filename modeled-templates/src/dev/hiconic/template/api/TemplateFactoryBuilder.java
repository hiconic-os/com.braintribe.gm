package dev.hiconic.template.api;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import com.braintribe.model.processing.meta.cmd.CmdResolver;

import dev.hiconic.template.model.core.TemplateEvaluationDefaults;

public final class TemplateFactoryBuilder {
	private final List<Consumer<TemplateExpertRegistry>> configurers = new ArrayList<>();
	private TemplateParserOptions options = TemplateParserOptions.STRICT;
	private String rootVariable = "input";
	private CmdResolver inputCmdResolver;
	private CmdResolver expertCmdResolver;
	private boolean allowNoEscape = true;
	private TemplateEvaluationDefaults defaults = TemplateDefaults.standard();

	public TemplateFactoryBuilder configure(Consumer<TemplateExpertRegistry> configurer) {
		configurers.add(Objects.requireNonNull(configurer, "configurer"));
		return this;
	}

	public TemplateFactoryBuilder options(TemplateParserOptions options) {
		this.options = Objects.requireNonNull(options, "options");
		return this;
	}

	public TemplateFactoryBuilder rootVariable(String rootVariable) {
		this.rootVariable = Objects.requireNonNull(rootVariable, "rootVariable");
		return this;
	}

	public TemplateFactoryBuilder cmdResolver(CmdResolver cmdResolver) {
		this.inputCmdResolver = cmdResolver;
		this.expertCmdResolver = cmdResolver;
		return this;
	}

	public TemplateFactoryBuilder inputCmdResolver(CmdResolver inputCmdResolver) {
		this.inputCmdResolver = inputCmdResolver;
		return this;
	}

	public TemplateFactoryBuilder expertCmdResolver(CmdResolver expertCmdResolver) {
		this.expertCmdResolver = expertCmdResolver;
		return this;
	}

	public TemplateFactoryBuilder allowNoEscape(boolean allowNoEscape) {
		this.allowNoEscape = allowNoEscape;
		return this;
	}

	public TemplateFactoryBuilder defaults(TemplateEvaluationDefaults defaults) {
		this.defaults = Objects.requireNonNull(defaults, "defaults");
		return this;
	}

	public TemplateFactoryBuilder defaultLocale(Locale locale) {
		this.defaults = TemplateDefaults.derive(defaults).locale(locale).build();
		return this;
	}

	public TemplateFactoryBuilder defaultZone(ZoneId zone) {
		this.defaults = TemplateDefaults.derive(defaults).zone(zone).build();
		return this;
	}

	public TemplateFactoryBuilder defaultDateFormat(String datePattern) {
		this.defaults = TemplateDefaults.derive(defaults).datePattern(datePattern).build();
		return this;
	}

	public TemplateFactoryBuilder defaultNumberFormat(String numberPattern) {
		this.defaults = TemplateDefaults.derive(defaults).numberPattern(numberPattern).build();
		return this;
	}

	public TemplateFactory build() {
		return new TemplateFactory(TemplateFactory.lazyRegistry(configurers), options, rootVariable,
				inputCmdResolver, expertCmdResolver, allowNoEscape, defaults);
	}
}

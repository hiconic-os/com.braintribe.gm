package dev.hiconic.template.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.api.ResolvedTemplateDefaults;
import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateDefaults;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;
import dev.hiconic.template.model.core.TemplateNode;

public class ModeledTemplate<I> implements Template<I> {
	private TemplateNode rootNode;
	private final GenericModelType rootType;
	private final String rootVariable;
	private final ConfigurableTemplateExpertRegistry registry;
	private final boolean allowNoEscape;
	private final TemplateEvaluationDefaults defaults;
	private final ResolvedTemplateDefaults resolvedDefaults;

	public ModeledTemplate(TemplateNode rootNode, GenericModelType rootType, String rootVariable,
			ConfigurableTemplateExpertRegistry registry, boolean allowNoEscape) {
		this(rootNode, rootType, rootVariable, registry, allowNoEscape, TemplateDefaults.standard());
	}

	public ModeledTemplate(TemplateNode rootNode, GenericModelType rootType, String rootVariable,
			ConfigurableTemplateExpertRegistry registry, boolean allowNoEscape, TemplateEvaluationDefaults defaults) {
		this.rootNode = Objects.requireNonNull(rootNode, "rootNode");
		this.rootType = Objects.requireNonNull(rootType, "rootType");
		this.rootVariable = Objects.requireNonNull(rootVariable, "rootVariable");
		this.registry = Objects.requireNonNull(registry, "registry");
		this.allowNoEscape = allowNoEscape;
		this.defaults = Objects.requireNonNull(defaults, "defaults");
		this.resolvedDefaults = ResolvedTemplateDefaults.of(defaults);
	}

	@Override
	public GenericModelType rootType() {
		return rootType;
	}

	@Override
	public String rootVariable() {
		return rootVariable;
	}

	@Override
	public TemplateNode rootNode() {
		return rootNode;
	}

	@Override
	public void rootNode(TemplateNode rootNode) {
		this.rootNode = Objects.requireNonNull(rootNode, "rootNode");
	}

	@Override
	public void evaluate(I input, OutputStream output, Charset charset) throws IOException {
		if (!rootType.isValueAssignable(input))
			throw new IllegalArgumentException("Template input must be assignable to " + rootType.getTypeSignature()
					+ " but was " + input.getClass().getName());
		RuntimeTemplateEvaluationContext context =
				new RuntimeTemplateEvaluationContext(registry, output, charset, allowNoEscape, defaults, resolvedDefaults);
		context.declareReadonlyVariable(rootVariable, input);
		context.evaluate(rootNode);
		context.flush();
	}
}

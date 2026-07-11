package dev.hiconic.template.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Objects;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.bvd.navigation.PropertyPath;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;

import dev.hiconic.template.api.ResolvedTemplateDefaults;
import dev.hiconic.template.api.TemplateDefaults;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.TemplateEvaluationDefaults;
import dev.hiconic.template.model.core.TemplateNode;

public class RuntimeTemplateEvaluationContext extends AbstractScopedTemplateEvaluationContext {
	private final ConfigurableTemplateExpertRegistry registry;
	private final OutputStream output;
	private final Charset charset;
	private final boolean allowNoEscape;
	private final TemplateEvaluationDefaults defaults;
	private final ResolvedTemplateDefaults resolvedDefaults;

	public RuntimeTemplateEvaluationContext(ConfigurableTemplateExpertRegistry registry, OutputStream output,
			Charset charset, boolean allowNoEscape) {
		this(registry, output, charset, allowNoEscape, TemplateDefaults.standard());
	}

	public RuntimeTemplateEvaluationContext(ConfigurableTemplateExpertRegistry registry, OutputStream output,
			Charset charset, boolean allowNoEscape, TemplateEvaluationDefaults defaults) {
		this(registry, output, charset, allowNoEscape, defaults, ResolvedTemplateDefaults.of(defaults));
	}

	public RuntimeTemplateEvaluationContext(ConfigurableTemplateExpertRegistry registry, OutputStream output,
			Charset charset, boolean allowNoEscape, TemplateEvaluationDefaults defaults,
			ResolvedTemplateDefaults resolvedDefaults) {
		this.registry = Objects.requireNonNull(registry, "registry");
		this.output = Objects.requireNonNull(output, "output");
		this.charset = Objects.requireNonNull(charset, "charset");
		this.allowNoEscape = allowNoEscape;
		this.defaults = Objects.requireNonNull(defaults, "defaults");
		this.resolvedDefaults = Objects.requireNonNull(resolvedDefaults, "resolvedDefaults");
	}

	@Override
	public Object evaluate(ValueDescriptor vd) {
		Objects.requireNonNull(vd, "vd");
		if (vd instanceof Variable variable)
			return getVariable(variable.getName());
		if (vd instanceof PropertyPath propertyPath)
			return evaluatePropertyPath(propertyPath);

		VdEvaluator<ValueDescriptor, Object> evaluator = vdEvaluator(vd);
		if (evaluator == null)
			throw new IllegalArgumentException("No value descriptor evaluator registered for "
					+ vd.entityType().getTypeSignature());
		Maybe<Object> maybe = evaluator.transform(this, vd);
		if (maybe.isUnsatisfied())
			throw new IllegalArgumentException(maybe.whyUnsatisfied().stringify());
		return maybe.get();
	}

	@Override
	public void evaluate(TemplateNode node) {
		Objects.requireNonNull(node, "node");
		TemplateNodeEvaluator<TemplateNode> evaluator = nodeEvaluator(node);
		if (evaluator == null)
			throw new IllegalArgumentException("No template node evaluator registered for "
					+ node.entityType().getTypeSignature());
		ScopedValue.where(CURRENT, this).run(() -> evaluator.evaluate(this, node));
	}

	@Override
	public void append(String text) {
		try {
			output.write(text.getBytes(charset));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public boolean allowsNoEscape() {
		return allowNoEscape;
	}

	@Override
	public TemplateEvaluationDefaults defaults() {
		return defaults;
	}

	@Override
	public ResolvedTemplateDefaults resolvedDefaults() {
		return resolvedDefaults;
	}

	public void flush() throws IOException {
		output.flush();
	}

	private Object evaluatePropertyPath(PropertyPath propertyPath) {
		Object current = propertyPath.getEntity();
		if (current instanceof ValueDescriptor descriptor)
			current = evaluate(descriptor);

		for (String segment : propertyPath.getPropertyPath().split("\\.")) {
			if (!(current instanceof GenericEntity entity))
				throw new IllegalArgumentException("Cannot access property '" + segment + "' on "
						+ (current == null ? "null" : current.getClass().getName()));
			Property property = entity.entityType().findProperty(segment);
			if (property == null)
				throw new IllegalArgumentException("Unknown property '" + segment + "' on "
						+ entity.entityType().getTypeSignature());
			current = property.get(entity);
			if (current instanceof ValueDescriptor descriptor)
				current = evaluate(descriptor);
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	private TemplateNodeEvaluator<TemplateNode> nodeEvaluator(TemplateNode node) {
		return (TemplateNodeEvaluator<TemplateNode>) registry.findEvaluator(node.entityType());
	}

	@SuppressWarnings("unchecked")
	private VdEvaluator<ValueDescriptor, Object> vdEvaluator(ValueDescriptor descriptor) {
		return (VdEvaluator<ValueDescriptor, Object>) registry.findVdEvaluator(descriptor.entityType());
	}
}

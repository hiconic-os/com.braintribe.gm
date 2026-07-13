package dev.hiconic.template.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.ReasonException;
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
import dev.hiconic.template.model.evaluation.NullPathElement;
import dev.hiconic.template.model.evaluation.PathEvaluationError;
import dev.hiconic.template.model.core.vd.TemplateVariable;
import dev.hiconic.template.model.core.vd.TemplatePropertyPath;
import dev.hiconic.template.model.core.path.PropertyAccess;
import dev.hiconic.template.model.core.path.ListIndexAccess;
import dev.hiconic.template.model.core.path.MapKeyAccess;
import dev.hiconic.template.model.core.path.PathAccess;

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
		if (vd instanceof TemplateVariable variable)
			return getVariable(variable.getSymbol());
		if (vd instanceof TemplatePropertyPath propertyPath)
			return evaluateTemplatePropertyPath(propertyPath);
		if (vd instanceof Variable variable)
			return getVariable(variable.getName());

		VdEvaluator<ValueDescriptor, Object> evaluator = vdEvaluator(vd);
		if (evaluator == null)
			throw new IllegalArgumentException("No value descriptor evaluator registered for "
					+ vd.entityType().getTypeSignature());
		Maybe<Object> maybe = evaluator.transform(this, vd);
		if (maybe.isUnsatisfied())
			throw new ReasonException(maybe.whyUnsatisfied());
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

	private Object evaluateTemplatePropertyPath(TemplatePropertyPath path) {
		Object current = evaluate(path.getRoot());
		String fullPath = TemplatePaths.render(path);
		for (var step : path.getAccesses()) {
			String segment = segment(step);
			if (current == null) {
				if (optional(step)) return null;
				throw new ReasonException(NullPathElement.create(fullPath, segment, "read", range(path, step)));
			}
			if (step instanceof PropertyAccess access) {
				if (!(current instanceof GenericEntity entity))
				throw new ReasonException(PathEvaluationError.create(fullPath, segment, "read",
						"Receiver of '" + segment + "' in path '" + fullPath + "' is not an entity", range(path, step)));
				Property property = access.getProperty().getResolvedProperty();
				if (property == null) {
					property = entity.entityType().findProperty(segment);
					if (property == null) throw new ReasonException(PathEvaluationError.create(fullPath, segment, "read",
							"Unknown property '" + segment + "' in path '" + fullPath + "'", range(path, step)));
					access.getProperty().setResolvedProperty(property);
				}
				current = property.get(entity);
			} else if (step instanceof ListIndexAccess access) {
				Object indexValue = evaluateAccessValue(access, ListIndexAccess.T.findProperty("index"));
				if (!(current instanceof List<?> list) || !(indexValue instanceof Integer index))
					throw accessError(path, step, fullPath, segment, "List access requires a list receiver and integer index");
				if (index < 0 || index >= list.size()) {
					if (access.getOptional()) return null;
					throw accessError(path, step, fullPath, segment, "List index " + index + " is out of bounds");
				}
				current = list.get(index);
			} else if (step instanceof MapKeyAccess access) {
				if (!(current instanceof Map<?, ?> map))
					throw accessError(path, step, fullPath, segment, "Map-key access requires a map receiver");
				Object key = evaluateAccessValue(access, MapKeyAccess.T.findProperty("key"));
				if (!map.containsKey(key)) {
					if (access.getOptional()) return null;
					throw accessError(path, step, fullPath, segment, "Map key does not exist");
				}
				current = map.get(key);
			} else {
				throw accessError(path, step, fullPath, segment, "Unsupported path access");
			}
			current = TemplateValues.evaluate(this, current);
		}
		return current;
	}

	private Object evaluateAccessValue(GenericEntity access, Property property) {
		ValueDescriptor descriptor = property.getVdDirect(access);
		return descriptor == null ? property.getDirect(access) : evaluate(descriptor);
	}

	private ReasonException accessError(TemplatePropertyPath path, PathAccess access, String fullPath,
			String segment, String message) {
		return new ReasonException(PathEvaluationError.create(fullPath, segment, "read",
				message + " in path '" + fullPath + "'", range(path, access)));
	}

	private static boolean optional(PathAccess access) {
		return access instanceof PropertyAccess property && property.getOptional()
				|| access instanceof ListIndexAccess index && index.getOptional()
				|| access instanceof MapKeyAccess key && key.getOptional();
	}

	private static String segment(PathAccess access) {
		return TemplatePaths.segment(access);
	}

	private static dev.hiconic.template.model.parse.TextRange range(TemplatePropertyPath path, PathAccess access) {
		return access.getSourceRange() == null ? path.getSourceRange() : access.getSourceRange();
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

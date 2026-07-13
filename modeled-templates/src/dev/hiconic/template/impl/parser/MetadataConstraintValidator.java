package dev.hiconic.template.impl.parser;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.meta.data.constraint.Limit;
import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.meta.data.constraint.Max;
import com.braintribe.model.meta.data.constraint.MaxLength;
import com.braintribe.model.meta.data.constraint.Min;
import com.braintribe.model.meta.data.constraint.MinLength;
import com.braintribe.model.meta.data.constraint.Pattern;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.builders.PropertyMdResolver;

import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.model.parse.TextRange;
import dev.hiconic.template.api.ValidationContext;

/** Applies constraints once to parse-time known direct values. */
final class MetadataConstraintValidator {
	private final List<CmdResolver> resolvers;
	private final IdentityHashMap<GenericEntity, Boolean> validated = new IdentityHashMap<>();
	private TextRange range;
	private ValidationContext context;

	MetadataConstraintValidator(CmdResolver inputResolver, CmdResolver expertResolver) {
		this.resolvers = inputResolver == null
				? expertResolver == null ? List.of() : List.of(expertResolver)
				: expertResolver == null || expertResolver == inputResolver
						? List.of(inputResolver) : List.of(inputResolver, expertResolver);
	}

	void reset() {
		validated.clear();
		range = null;
		context = null;
	}

	Reason validate(ValidationContext context, GenericEntity root, TextRange range) {
		this.context = context;
		this.range = range;
		return visit(root, root.entityType().getShortName());
	}

	private Reason visit(GenericEntity entity, String modelPath) {
		if (validated.put(entity, Boolean.TRUE) != null) return null;
		for (Property property : entity.entityType().getProperties()) {
			ValueDescriptor descriptor = property.getVdDirect(entity);
			String propertyPath = modelPath + "." + property.getName();
			if (descriptor != null) {
				Reason nested = visit(descriptor, propertyPath);
				if (nested != null) return nested;
				continue;
			}

			Object value = property.getDirect(entity);
			PropertyMdResolver md = metadata(entity, property);
			if (md != null) {
				Reason violation = validateDirect(md, value, propertyPath,
						context.getRange(entity, property));
				if (violation != null) return violation;
			}
			Reason nested = visitValue(value, propertyPath);
			if (nested != null) return nested;
		}
		return null;
	}

	private Reason visitValue(Object value, String modelPath) {
		if (value instanceof GenericEntity entity) return visit(entity, modelPath);
		if (value instanceof Collection<?> collection) {
			int index = 0;
			for (Object element : collection) {
				Reason nested = visitValue(element, modelPath + "[" + index++ + "]");
				if (nested != null) return nested;
			}
		} else if (value instanceof Map<?, ?> map) {
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Reason nested = visitValue(entry.getKey(), modelPath + "[key]");
				if (nested == null) nested = visitValue(entry.getValue(), modelPath + "[" + entry.getKey() + "]");
				if (nested != null) return nested;
			}
		}
		return null;
	}

	private PropertyMdResolver metadata(GenericEntity entity, Property property) {
		for (CmdResolver resolver : resolvers) {
			if (resolver.getModelOracle().findEntityTypeOracle(entity.entityType()) != null)
				return resolver.getMetaData().lenient(true).entity(entity).property(property);
		}
		return null;
	}

	private Reason validateDirect(PropertyMdResolver md, Object value, String path, TextRange propertyRange) {
		if (value == null)
			return md.is(Mandatory.T) ? violation(path, "mandatory property is null", propertyRange) : null;

		if (value instanceof String string) {
			Pattern pattern = md.meta(Pattern.T).exclusive();
			if (pattern != null) {
				try {
					if (!string.matches(pattern.getExpression()))
						return violation(path, "value does not match pattern '" + pattern.getExpression() + "'", propertyRange);
				} catch (PatternSyntaxException e) {
					return violation(path, "metadata contains invalid pattern '" + pattern.getExpression() + "': " + e.getMessage(), propertyRange);
				}
			}
			MinLength minLength = md.meta(MinLength.T).exclusive();
			if (minLength != null && string.length() < minLength.getLength())
				return violation(path, "length " + string.length() + " is below minimum " + minLength.getLength(), propertyRange);
			MaxLength maxLength = md.meta(MaxLength.T).exclusive();
			if (maxLength != null && string.length() > maxLength.getLength())
				return violation(path, "length " + string.length() + " exceeds maximum " + maxLength.getLength(), propertyRange);
		}

		if (value instanceof Number number) {
			Min min = md.meta(Min.T).exclusive();
			if (min != null && violatesMin(number, min))
				return violation(path, "value " + number + " violates " + limitText("minimum", min), propertyRange);
			Max max = md.meta(Max.T).exclusive();
			if (max != null && violatesMax(number, max))
				return violation(path, "value " + number + " violates " + limitText("maximum", max), propertyRange);
		}
		return null;
	}

	private static boolean violatesMin(Number value, Min min) {
		int comparison = decimal(value).compareTo(decimal(min.getLimit()));
		return min.getExclusive() ? comparison <= 0 : comparison < 0;
	}

	private static boolean violatesMax(Number value, Max max) {
		int comparison = decimal(value).compareTo(decimal(max.getLimit()));
		return max.getExclusive() ? comparison >= 0 : comparison > 0;
	}

	private static BigDecimal decimal(Object value) {
		if (value instanceof BigDecimal decimal) return decimal;
		if (!(value instanceof Number number))
			throw new IllegalArgumentException("Numeric limit is not a Number: " + value);
		return new BigDecimal(number.toString());
	}

	private static String limitText(String name, Limit limit) {
		return (limit.getExclusive() ? "exclusive " : "") + name + " " + limit.getLimit();
	}

	private Reason violation(String path, String message, TextRange propertyRange) {
		TemplateParseError reason = TemplateParseError.T.create();
		reason.setText("Constraint violation at " + path + ": " + message);
		reason.setModelPath(path);
		reason.setRange(propertyRange == null ? range : propertyRange);
		return reason;
	}
}

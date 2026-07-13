package dev.hiconic.template.impl.node;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.generic.reflection.SimpleTypes;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.gm.model.reason.ReasonException;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.impl.TemplateValues;
import dev.hiconic.template.model.core.instr.Add;
import dev.hiconic.template.model.core.instr.Append;
import dev.hiconic.template.model.core.instr.AssignmentTarget;
import dev.hiconic.template.model.core.instr.CollectionMutation;
import dev.hiconic.template.model.core.instr.Insert;
import dev.hiconic.template.model.core.instr.PropertyAssignmentTarget;
import dev.hiconic.template.model.core.instr.Put;
import dev.hiconic.template.model.core.instr.Remove;
import dev.hiconic.template.model.core.instr.VariableAssignmentTarget;

public class CollectionMutationEvaluator<N extends CollectionMutation> implements TemplateNodeEvaluator<N> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, N node, Property property) {
		GenericModelType targetType = targetType(context, node);
		if (node instanceof Insert && property == Insert.index.property()) return SimpleTypes.TYPE_INTEGER;
		if (targetType instanceof MapType map) {
			if (node instanceof Put && property == Put.key.property()
					|| node instanceof Remove && property == Remove.value.property()) return map.getKeyType();
			if (node instanceof Put && property == Put.value.property()) return map.getValueType();
		}
		if (targetType instanceof CollectionType collection && valueProperty(node) == property)
			return collection.getCollectionElementType();
		return null;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void evaluate(TemplateEvaluationContext context, N node) {
		Object target = targetValue(context, node.getTarget());
		if (node instanceof Append append) {
			if (!(target instanceof List list)) throw runtime("Append target is not a list");
			list.add(value(context, append, Append.value.property()));
		} else if (node instanceof Insert insert) {
			if (!(target instanceof List list)) throw runtime("Insert target is not a list");
			Object indexValue = value(context, insert, Insert.index.property());
			if (!(indexValue instanceof Integer index) || index < 0 || index > list.size())
				throw runtime("Insert index " + indexValue + " is outside 0.." + list.size());
			list.add(index, value(context, insert, Insert.value.property()));
		} else if (node instanceof Add add) {
			if (!(target instanceof java.util.Set set)) throw runtime("Add target is not a set");
			set.add(value(context, add, Add.value.property()));
		} else if (node instanceof Put put) {
			if (!(target instanceof Map map)) throw runtime("Put target is not a map");
			map.put(value(context, put, Put.key.property()), value(context, put, Put.value.property()));
		} else if (node instanceof Remove remove) {
			Object value = value(context, remove, Remove.value.property());
			if (target instanceof Map map) map.remove(value);
			else if (target instanceof Collection collection) collection.remove(value);
			else throw runtime("Remove target is neither a collection nor a map");
		} else throw runtime("Unsupported collection mutation: " + node.entityType().getTypeSignature());
	}

	@Override
	public Reason validate(ValidationContext context, N node) {
		if (node.getTarget() == null)
			return InvalidArgument.create(node.entityType().getShortName() + ".target must not be null");
		GenericModelType targetType = targetType(context, node);
		if (!acceptsTarget(node, targetType))
			return InvalidArgument.create(node.entityType().getShortName() + " target has incompatible type "
					+ (targetType == null ? "<unknown>" : targetType.getTypeSignature()));
		for (Property property : valueProperties(node)) {
			if (context.getRange(node, property) == null)
				return InvalidArgument.create(node.entityType().getShortName() + "." + property.getName() + " is required");
			if (context.isExplicitNull(node, property)) continue;
			GenericModelType expected = expectedArgumentType(context, node, property);
			GenericModelType actual = context.getType(node, property);
			if (expected != null && (actual == null || !expected.isAssignableFrom(actual)))
				return InvalidArgument.create(node.entityType().getShortName() + "." + property.getName()
						+ " expects " + expected.getTypeSignature() + " but got "
						+ (actual == null ? "<unknown>" : actual.getTypeSignature()));
		}
		return null;
	}

	private static GenericModelType targetType(ValidationContext context, CollectionMutation node) {
		return node.getTarget() == null ? null : context.resolveType(node.getTarget().getTypeSignature());
	}

	private static boolean acceptsTarget(CollectionMutation node, GenericModelType type) {
		if (node instanceof Append || node instanceof Insert) return type instanceof ListType;
		if (node instanceof Add) return type instanceof SetType;
		if (node instanceof Put) return type instanceof MapType;
		return node instanceof Remove && (type instanceof CollectionType || type instanceof MapType);
	}

	private static Property valueProperty(CollectionMutation node) {
		if (node instanceof Append) return Append.value.property();
		if (node instanceof Insert) return Insert.value.property();
		if (node instanceof Add) return Add.value.property();
		if (node instanceof Remove) return Remove.value.property();
		return null;
	}

	private static List<Property> valueProperties(CollectionMutation node) {
		if (node instanceof Insert) return List.of(Insert.index.property(), Insert.value.property());
		if (node instanceof Put) return List.of(Put.key.property(), Put.value.property());
		return List.of(valueProperty(node));
	}

	private static Object targetValue(TemplateEvaluationContext context, AssignmentTarget target) {
		if (target instanceof VariableAssignmentTarget variable) return context.getVariable(variable.getSymbol());
		if (target instanceof PropertyAssignmentTarget property) return context.evaluate(property.getPath());
		throw runtime("Unsupported collection assignment target: " + target);
	}

	private static Object value(TemplateEvaluationContext context, GenericEntity entity, Property property) {
		ValueDescriptor descriptor = property.getVdDirect(entity);
		return TemplateValues.evaluate(context, descriptor == null ? property.getDirect(entity) : context.evaluate(descriptor));
	}

	private static ReasonException runtime(String message) {
		return new ReasonException(InvalidArgument.create(message));
	}
}

package dev.hiconic.template.impl.parser;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.bvd.navigation.PropertyPath;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;
import com.braintribe.model.generic.value.Escape;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.api.TemplateNodeEvaluator;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.TemplateNode;

public class ExpertModelCompleter {
	private final TemplateExpertRegistry registry;

	public ExpertModelCompleter(TemplateExpertRegistry registry) {
		this.registry = registry;
	}

	public Reason completeAndValidate(ValidationContext context, TemplateNode root) {
		IdentityHashMap<GenericEntity, Boolean> visited = new IdentityHashMap<>();
		return visit(context, root, visited);
	}

	private Reason visit(ValidationContext context, GenericEntity entity, IdentityHashMap<GenericEntity, Boolean> visited) {
		if (visited.put(entity, Boolean.TRUE) != null)
			return null;
		if (entity instanceof Escape)
			return null;

		for (Property property : entity.entityType().getProperties()) {
			ValueDescriptor descriptor = property.getVdDirect(entity);
			if (descriptor != null) {
				Reason reason = completeDescriptor(context, descriptor, visited);
				if (reason != null)
					return atProperty(entity, property, reason);

				GenericModelType valueType = descriptor.valueType();
				if (!property.getType().isAssignableFrom(valueType))
					return InvalidArgument.create(entity.entityType().getShortName() + "." + property.getName()
							+ " expects " + property.getType().getTypeSignature() + " but descriptor evaluates to "
							+ valueType.getTypeSignature());
				continue;
			}

			Object value = property.getDirect(entity);
			Reason reason = visitValue(context, value, property.getType(), visited);
			if (reason != null)
				return atProperty(entity, property, reason);
		}

		if (entity instanceof TemplateNode) {
			TemplateNode node = (TemplateNode) entity;
			TemplateNodeEvaluator<TemplateNode> evaluator = nodeEvaluator(node);
			if (evaluator != null)
				return evaluator.complete(context, node);
		}

		return null;
	}

	private Reason visitValue(ValidationContext context, Object value, GenericModelType expectedType,
			IdentityHashMap<GenericEntity, Boolean> visited) {
		ValueDescriptor heldDescriptor = VdHolder.getValueDescriptorIfPossible(value);
		if (heldDescriptor != null) {
			ValueDescriptor descriptor = heldDescriptor;
			Reason reason = completeDescriptor(context, descriptor, visited);
			if (reason != null)
				return reason;
			if (expectedType != null && !expectedType.isAssignableFrom(descriptor.valueType()))
				return InvalidArgument.create("Value expects " + expectedType.getTypeSignature()
						+ " but descriptor evaluates to " + descriptor.valueType().getTypeSignature());
			return null;
		}
		if (value instanceof GenericEntity) {
			GenericEntity nested = (GenericEntity) value;
			return visit(context, nested, visited);
		}
		if (value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) value;
			GenericModelType elementType = collectionElementType(expectedType);
			for (Object element : collection) {
				Reason reason = visitValue(context, element, elementType, visited);
				if (reason != null)
					return reason;
			}
		} else if (value instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) value;
			MapType mapType = expectedType instanceof MapType ? (MapType) expectedType : null;
			GenericModelType keyType = mapType == null ? null : mapType.getKeyType();
			GenericModelType valueType = mapType == null ? null : mapType.getValueType();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Reason reason = visitValue(context, entry.getKey(), keyType, visited);
				if (reason == null)
					reason = visitValue(context, entry.getValue(), valueType, visited);
				if (reason != null)
					return reason;
			}
		}
		return null;
	}

	private Reason completeDescriptor(ValidationContext context, ValueDescriptor descriptor,
			IdentityHashMap<GenericEntity, Boolean> visited) {
		if (descriptor instanceof Escape || descriptor instanceof Variable || descriptor instanceof PropertyPath)
			return null;

		Reason nested = visit(context, descriptor, visited);
		if (nested != null)
			return nested;

		VdEvaluator<ValueDescriptor, Object> evaluator = vdEvaluator(descriptor);
		if (evaluator == null)
			return descriptor.valueType().isBase()
					? InvalidArgument.create("No completion expert registered for " + descriptor.entityType().getTypeSignature())
					: null;

		return evaluator.complete(context, descriptor);
	}

	private GenericModelType collectionElementType(GenericModelType type) {
		return type instanceof CollectionType ? ((CollectionType) type).getCollectionElementType() : null;
	}

	private Reason atProperty(GenericEntity entity, Property property, Reason cause) {
		InvalidArgument reason = InvalidArgument.create(
				"Invalid value at " + entity.entityType().getShortName() + "." + property.getName());
		reason.causedBy(cause);
		return reason;
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

package dev.hiconic.template.impl.parser;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import dev.hiconic.template.model.core.vd.TemplatePropertyPath;
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
import dev.hiconic.template.model.core.instr.InvokeInstruction;
import dev.hiconic.template.model.parse.TemplateParseError;
import dev.hiconic.template.model.parse.TextRange;

public class ExpertModelCompleter {
	private final TemplateExpertRegistry registry;

	public ExpertModelCompleter(TemplateExpertRegistry registry) {
		this.registry = registry;
	}

	public Reason completeAndValidate(ValidationContext context, TemplateNode root) {
		IdentityHashMap<GenericEntity, Boolean> visited = new IdentityHashMap<>();
		return visit(context, root, root.entityType().getShortName(), visited);
	}

	private Reason visit(ValidationContext context, GenericEntity entity, String modelPath,
			IdentityHashMap<GenericEntity, Boolean> visited) {
		if (visited.put(entity, Boolean.TRUE) != null)
			return null;
		if (entity instanceof Escape)
			return null;

		for (Property property : entity.entityType().getProperties()) {
			String propertyPath = modelPath + "." + property.getName();
			// A declared instruction may be forward-referenced while its body is
			// still being parsed. Its signature is already complete; the declaration
			// itself is completed when it occurs in the surrounding template.
			if (entity instanceof InvokeInstruction && InvokeInstruction.declaration.name().equals(property.getName()))
				continue;
			ValueDescriptor descriptor = property.getVdDirect(entity);
			if (descriptor != null) {
				Reason reason = completeDescriptor(context, descriptor, propertyPath, visited);
				if (reason != null)
					return atProperty(context, entity, property, propertyPath, reason);

				GenericModelType valueType = context.getType(entity, property);
				if (!property.getType().isAssignableFrom(valueType))
					return atProperty(context, entity, property, propertyPath,
							InvalidArgument.create(entity.entityType().getShortName() + "." + property.getName()
							+ " expects " + property.getType().getTypeSignature() + " but descriptor evaluates to "
							+ valueType.getTypeSignature()));
				continue;
			}

			Object value = property.getDirect(entity);
			Reason reason = visitValue(context, value, property.getType(), propertyPath, visited);
			if (reason != null)
				return atProperty(context, entity, property, propertyPath, reason);
		}

		if (entity instanceof TemplateNode) {
			TemplateNode node = (TemplateNode) entity;
			TemplateNodeEvaluator<TemplateNode> evaluator = nodeEvaluator(node);
			if (evaluator != null)
				return evaluator.complete(context, node);
		}

		return null;
	}

	private Reason visitValue(ValidationContext context, Object value, GenericModelType expectedType, String modelPath,
			IdentityHashMap<GenericEntity, Boolean> visited) {
		ValueDescriptor heldDescriptor = VdHolder.getValueDescriptorIfPossible(value);
		if (heldDescriptor != null) {
			ValueDescriptor descriptor = heldDescriptor;
			Reason reason = completeDescriptor(context, descriptor, modelPath, visited);
			if (reason != null)
				return reason;
			if (expectedType != null && !expectedType.isAssignableFrom(descriptor.valueType()))
				return InvalidArgument.create("Value expects " + expectedType.getTypeSignature()
						+ " but descriptor evaluates to " + descriptor.valueType().getTypeSignature());
			return null;
		}
		if (value instanceof GenericEntity) {
			GenericEntity nested = (GenericEntity) value;
			return visit(context, nested, modelPath, visited);
		}
		if (value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>) value;
			GenericModelType elementType = collectionElementType(expectedType);
			int index = 0;
			for (Object element : collection) {
				Reason reason = visitValue(context, element, elementType, modelPath + "[" + index++ + "]", visited);
				if (reason != null)
					return reason;
			}
		} else if (value instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) value;
			MapType mapType = expectedType instanceof MapType ? (MapType) expectedType : null;
			GenericModelType keyType = mapType == null ? null : mapType.getKeyType();
			GenericModelType valueType = mapType == null ? null : mapType.getValueType();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Reason reason = visitValue(context, entry.getKey(), keyType, modelPath + "[key]", visited);
				if (reason == null)
					reason = visitValue(context, entry.getValue(), valueType, modelPath + "[" + entry.getKey() + "]", visited);
				if (reason != null)
					return reason;
			}
		}
		return null;
	}

	private Reason completeDescriptor(ValidationContext context, ValueDescriptor descriptor, String modelPath,
			IdentityHashMap<GenericEntity, Boolean> visited) {
		if (descriptor instanceof Escape || descriptor instanceof Variable || descriptor instanceof TemplatePropertyPath)
			return null;

		Reason nested = visit(context, descriptor, modelPath, visited);
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

	private Reason atProperty(ValidationContext context, GenericEntity entity, Property property,
			String modelPath, Reason cause) {
		TextRange range = context.getRange(entity, property);
		if (range == null) {
			InvalidArgument reason = InvalidArgument.create("Invalid value at " + modelPath);
			reason.causedBy(cause);
			return reason;
		}
		TemplateParseError reason = TemplateParseError.T.create();
		reason.setText("Invalid value at " + modelPath + " at line " + range.getStart().getLine()
				+ ", column " + range.getStart().getColumn());
		reason.setModelPath(modelPath);
		reason.setRange(range);
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

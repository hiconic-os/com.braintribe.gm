package dev.hiconic.template.impl.vd;

import java.util.Collection;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

import dev.hiconic.template.api.TemplateEvaluationContext;
import dev.hiconic.template.api.ValidationContext;
import dev.hiconic.template.api.VdEvaluator;
import dev.hiconic.template.model.core.vd.Contains;

public class ContainsEvaluator implements VdEvaluator<Contains, Boolean> {
	@Override
	public GenericModelType expectedArgumentType(ValidationContext context, Contains vd, Property property) {
		GenericModelType collectionType = context.getType(vd, Contains.collection);
		if (property == Contains.element.property() && collectionType instanceof CollectionType collection)
			return collection.getCollectionElementType();
		return null;
	}

	@Override
	public Maybe<Boolean> transform(TemplateEvaluationContext context, Contains vd) {
		Object collection = vd.getCollection();
		return collection instanceof Collection<?> values
				? Maybe.complete(values.contains(vd.getElement()))
				: Maybe.empty(InvalidArgument.create("Contains.collection is not a collection"));
	}

	@Override
	public Reason validate(ValidationContext context, Contains vd) {
		GenericModelType collectionType = context.getType(vd, Contains.collection);
		if (!(collectionType instanceof CollectionType collection)
				|| collection.getCollectionKind() == CollectionType.CollectionKind.map)
			return InvalidArgument.create("Contains.collection must evaluate to a collection, but evaluates to "
					+ typeSignature(collectionType));
		GenericModelType elementType = context.getType(vd, Contains.element);
		GenericModelType expected = collection.getCollectionElementType();
		if (!context.isExplicitNull(vd, Contains.element) && (elementType == null || !expected.isAssignableFrom(elementType)))
			return InvalidArgument.create("Contains.element expects " + expected.getTypeSignature()
					+ " but got " + typeSignature(elementType));
		return null;
	}

	private static String typeSignature(GenericModelType type) {
		return type == null ? "<unknown>" : type.getTypeSignature();
	}
}

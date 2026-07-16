package dev.hiconic.template.model.core.vd;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.generic.value.type.BooleanDescriptor;

/** Tests whether a collection contains an element. */
@PositionalArguments({"collection", "element"})
public interface Contains extends BooleanDescriptor {
	EntityType<Contains> T = EntityTypes.T(Contains.class);

	PropertyLiteral collection = PropertyLiteral.of(T, "collection");
	PropertyLiteral element = PropertyLiteral.of(T, "element");

	Object getCollection();
	void setCollection(Object collection);

	Object getElement();
	void setElement(Object element);
}

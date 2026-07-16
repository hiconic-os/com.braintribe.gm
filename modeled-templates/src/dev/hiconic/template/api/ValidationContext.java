package dev.hiconic.template.api;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import dev.hiconic.template.model.parse.TextRange;

public interface ValidationContext {
	GenericModelType getType(GenericEntity entity, Property property);

	/** True iff the parser supplied the property explicitly with the null literal. */
	default boolean isExplicitNull(GenericEntity entity, Property property) { return false; }
	default boolean isExplicitNull(GenericEntity entity, PropertyLiteral property) {
		return isExplicitNull(entity, property.property());
	}

	/** Exact source range of the value bound to this property, when available. */
	default TextRange getRange(GenericEntity entity, Property property) { return null; }
	default TextRange getRange(GenericEntity entity, PropertyLiteral property) {
		return getRange(entity, property.property());
	}

	default GenericModelType getType(GenericEntity entity, PropertyLiteral property) {
		return getType(entity, property.property());
	}

	default GenericModelType resolveType(String typeName) {
		return GMF.getTypeReflection().findType(typeName);
	}

	/** True iff the currently parsed/evaluated lexical block can consume a break signal. */
	default boolean canBreak() { return false; }

	/** True iff the currently parsed/evaluated lexical block can consume a continue signal. */
	default boolean canContinue() { return false; }

	/** Root type of a named template delegate visible to the parser, if any. */
	default GenericModelType templateRootType(String name) { return null; }
}

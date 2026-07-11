package com.braintribe.model.generic.reflection;

import java.util.Objects;

/**
 * A named, lazily resolved property bound to one entity type.
 * <p>
 * Model declarations can expose these literals next to their traditional
 * string constants. Resolution happens at most once in the steady state; a
 * concurrent first access may perform the same idempotent lookup more than
 * once.
 */
public final class PropertyLiteral implements CharSequence {
	private final EntityType<?> entityType;
	private final String name;
	private volatile Property property;

	private PropertyLiteral(EntityType<?> entityType, String name) {
		this.entityType = Objects.requireNonNull(entityType, "entityType");
		this.name = Objects.requireNonNull(name, "name");
	}

	public static PropertyLiteral of(EntityType<?> entityType, String name) {
		return new PropertyLiteral(entityType, name);
	}

	public Property property() {
		Property result = property;
		if (result == null) {
			result = entityType.getProperty(name);
			property = result;
		}
		return result;
	}

	public String name() {
		return name;
	}

	@Override
	public int length() {
		return name.length();
	}

	@Override
	public char charAt(int index) {
		return name.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return name.subSequence(start, end);
	}

	@Override
	public String toString() {
		return name;
	}
}

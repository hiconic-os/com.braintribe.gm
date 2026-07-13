package dev.hiconic.template.impl;

import dev.hiconic.template.model.core.path.PropertyAccess;
import dev.hiconic.template.model.core.path.ListIndexAccess;
import dev.hiconic.template.model.core.path.MapKeyAccess;
import dev.hiconic.template.model.core.vd.TemplatePropertyPath;
import dev.hiconic.template.model.core.vd.TemplateVariable;

final class TemplatePaths {
	private TemplatePaths() {
	}

	static String render(TemplatePropertyPath path) {
		StringBuilder result = new StringBuilder(path.getRoot() instanceof TemplateVariable variable
				? variable.getName() : "<expression>");
		for (var step : path.getAccesses()) {
			if (step instanceof PropertyAccess access)
				result.append(access.getOptional() ? "?." : ".")
						.append(access.getProperty().getSymbol().getName());
			else if (step instanceof ListIndexAccess access)
				result.append(access.getOptional() ? "?[" : "[").append(source(access, access.getIndex())).append(']');
			else if (step instanceof MapKeyAccess access)
				result.append(access.getOptional() ? "?[" : "[").append(source(access, access.getKey())).append(']');
			else
				result.append(".<access>");
		}
		return result.toString();
	}

	static String segment(dev.hiconic.template.model.core.path.PathAccess access) {
		if (access instanceof PropertyAccess property) return property.getProperty().getSymbol().getName();
		if (access instanceof ListIndexAccess index) return "[" + source(index, index.getIndex()) + "]";
		if (access instanceof MapKeyAccess key) return "[" + source(key, key.getKey()) + "]";
		return "<access>";
	}

	private static String source(dev.hiconic.template.model.core.path.PathAccess access, Object fallback) {
		return access.getSource() == null ? String.valueOf(fallback) : access.getSource();
	}
}

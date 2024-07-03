// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.generic.annotation.meta.api.synthesis;

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;
import static com.braintribe.utils.lcd.CollectionTools2.newTreeMap;
import static java.util.Collections.emptyMap;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.braintribe.model.generic.GenericEntity;

/**
 * @author peter.gazdik
 */
public class SingleAnnotationDescriptor extends AnnotationDescriptor {

	private Map<String, Object> annotationValues;

	public SingleAnnotationDescriptor(Class<? extends Annotation> annotationClass) {
		super(annotationClass);
	}

	public void addAnnotationValue(String name, Object value) {
		if (annotationValues == null)
			annotationValues = newTreeMap();

		annotationValues.put(name, value);
	}

	public Map<String, Object> getAnnotationValues() {
		return annotationValues != null ? annotationValues : emptyMap();
	}

	public void setGlobalId(String globalId) {
		addAnnotationValue(GenericEntity.globalId, globalId);
	}

	public String getGlobalId() {
		return (String) annotationValues.get(GenericEntity.globalId);
	}

	public void removeGlobalId() {
		annotationValues.remove(GenericEntity.globalId);
	}

	@Override
	public void withSourceCode(Consumer<String> sourceCodeConsumer) {
		sourceCodeConsumer.accept(toSourceCode());
	}

	private String toSourceCode() {
		String simpleName = annotationClass.getSimpleName();

		if (isEmpty(annotationValues))
			return simpleName;

		List<Entry<String, ?>> entries = annotationValues.entrySet().stream() //
				.filter(e -> e.getValue() != null) //
				.collect(Collectors.toList());

		if (entries.isEmpty())
			return simpleName;

		StringBuilder sb = new StringBuilder(simpleName);
		sb.append('(');

		boolean first = true;
		for (Entry<String, ?> entry : entries) {
			if (first)
				first = false;
			else
				sb.append(", ");

			appendElementWithValue(sb, entry);

		}

		sb.append(')');

		return sb.toString();
	}

	private void appendElementWithValue(StringBuilder sb, Entry<String, ?> entry) {
		String elementName = entry.getKey();
		Object value = entry.getValue();

		sb.append(elementName);
		sb.append('=');

		appendValue(sb, value);
	}

	private void appendValue(StringBuilder sb, Object value) {
		if (value != null && value.getClass().isArray()) {
			sb.append('{');
			boolean first = true;
			for (Object o : (Object[]) value) {
				if (first)
					first = false;
				else
					sb.append(", ");
				appendValue(sb, o);
			}

			sb.append('}');

		} else if (value instanceof String) {
			sb.append('"');
			String escapedValue = (String) value;
			escapedValue = escapedValue.replace("\\", "\\\\");
			escapedValue = escapedValue.replace("\"", "\\\"");
			escapedValue = escapedValue.replace("\n", "\\n");
			escapedValue = escapedValue.replace("\r", "\\r");
			sb.append(escapedValue);
			sb.append('"');

		} else if (value instanceof ClassReference) {
			ClassReference castedValue = (ClassReference) value;
			sb.append(castedValue.className);
			sb.append(".class");

		} else {
			sb.append(value);
		}
	}

}

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
package com.braintribe.gm.model.reason;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.template.Template;
import com.braintribe.model.generic.template.TemplateFragment;

public class ReasonFormatter {

	public static void format(Appendable builder, Reason reason, int depth) {
		format(builder, reason, depth, false);
	}
	
	public static void format(Appendable builder, Reason reason, int depth, boolean includeExceptions) {
		List<Throwable> exceptions = includeExceptions? new ArrayList<>(): null;
		
		format(builder, reason, depth, exceptions);
		
		try {
			if (exceptions != null && !exceptions.isEmpty()) {
				int num = 1;
				builder.append("\n");
				for (Throwable exception: exceptions) {
					builder.append("\n------------------------------------");
					builder.append("\nException #");
					builder.append(String.valueOf(num++));
					builder.append(":\n\n");
					builder.append(Exceptions.stringify(exception));
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static void format(Appendable builder, Reason reason, int depth, List<Throwable> exceptions) {
		try {
			writeIndentBulleted(builder, depth);
			
			if (reason == null) {
				builder.append("<null cause> - check code\n");
				return;
			}
			
			builder.append(reason.entityType().getShortName());
			
			String text = reason.getText();
			
			if (text != null) {
				builder.append(": ");
				writeTextIndented(builder, text, depth + 1);
			}
			
			if (exceptions != null) {
				Throwable throwable = reason.linkedThrowable();
				
				if (throwable != null) {
					exceptions.add(throwable);
					writeTextIndented(builder, "\n-> see Exception #" + exceptions.size(), depth + 1);
				}
			}

			List<Reason> attachedReasons = reason.getReasons();
			
			for (Reason cause: attachedReasons) {
				builder.append("\n");
				format(builder, cause, depth + 1, exceptions);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static void writeTextIndented(Appendable appendable, String text, int depth) throws IOException {
		int index = 0;
		
		do {
			int found = text.indexOf("\n", index);
			
			int end = found != -1? found + 1: text.length();

			if (index > 0) {
				writeIndent(appendable, depth);
			}
			
			appendable.append(text.substring(index, end));
			
			index = end;
		}
		while (index < text.length());
	}

	private static void writeIndent(Appendable builder, int depth) throws IOException {
		int i = 0;
				
		for (; i < depth; i++) {
			builder.append("  ");
		}
	}
	
	private static void writeIndentBulleted(Appendable builder, int depth) throws IOException {
		int i = 0;
				
		for (; i < depth - 1; i++) {
			builder.append("  ");
		}
		
		if (i < depth)
			builder.append("- ");
	}
	
	private static Map<EntityType<? extends Reason>, Template> templates = new ConcurrentHashMap<>();
	private static final Template missing = Template.parse("");

	private static Template resolveTemplate(Reason reason) {
		EntityType<Reason> entityType = reason.entityType();
		
		Template template = templates.computeIfAbsent(entityType, ReasonFormatter::_resolveTemplate);
		
		if (template == missing)
			return null;
		
		return template;
	}

	private static Template _resolveTemplate(@SuppressWarnings("unused") EntityType<? extends Reason> reasonType) {
//		SelectiveInformation si = reasonType.getJavaType().getAnnotation(SelectiveInformation.class);
//		
//		if (si == null)
//			return missing;
//		
//		return Template.parse(si.value());
		
		return missing;
	}
	
	public static String buildPlainText(Reason reason) {
		PlainMessageCollector collector = new PlainMessageCollector();
		
		if (!format(reason, collector))
			return null;
		
		return collector.getResult();
	}
	
	public static boolean format(Reason reason, ReasonMessageCollector collector) {
		Template template = resolveTemplate(reason);
		
		if (template == null)
			return false;
		
		formatTemplate(reason, template, collector);
		return true;
	}
	
	public static void formatTemplateExpression(Reason reason, String templateExpression, ReasonMessageCollector collector) {
		Template template = Template.parse(templateExpression);
		formatTemplate(reason, template, collector);
	}
	
	static void formatTemplate(Reason reason, Template template, ReasonMessageCollector collector) {
		Evaluation evaluation = new Evaluation(reason, collector);
		evaluation.evaluate(template);
	}

	private static class PlainMessageCollector implements ReasonMessageCollector {
		private StringBuilder builder = new StringBuilder();
		
		public String getResult() {
			return builder.toString();
		}

		@Override
		public void append(String text) {
			builder.append(text);
		}
		
		@Override
		public void appendProperty(GenericEntity entity, Property property, GenericModelType type, Object value) {
			outputValue(value, property.getType());
		}

		private void outputValue(Object value, GenericModelType type) {
			if (value == null) {
				builder.append("<n/a>");
				return;
			}
			
			switch (type.getTypeCode()) {
			case objectType:
				outputValue(value, type.getActualType(value));
				break;
				
			case booleanType:
			case dateType:
			case decimalType:
			case doubleType:
			case enumType:
			case floatType:
			case integerType:
			case longType:
			case stringType:
			case entityType:
				builder.append(value.toString());
				break;

			case listType:
			case setType: {
				int i = 0;
				GenericModelType eT = ((LinearCollectionType)type).getCollectionElementType(); 
				for (Object e : (Collection<?>)value) {
					if (i > 0)
						builder.append(", ");
					outputValue(e, eT);
					i++;
				}
				break;
			}
			case mapType: {
				int i = 0;
				MapType mapType = (MapType)type;
				
				GenericModelType kT = mapType.getKeyType(); 
				GenericModelType vT = mapType.getValueType(); 
				
				for (Map.Entry<?, ?> e : ((Map<?,?>)value).entrySet()) {
					if (i > 0)
						builder.append(", ");
				
					outputValue(e.getKey(), kT);
					builder.append(" = ");
					outputValue(e.getValue(), vT);
					i++;
				}
				break;
			}
			
			default:
				builder.append("<n/a>");
				break;
			}
		}
	}
	
	private static class Evaluation {
		private Reason reason;
		ReasonMessageCollector collector;

		public Evaluation(Reason reason, ReasonMessageCollector collector) {
			super();
			this.reason = reason;
			this.collector = collector;
		}

		private void evaluate(Template template) {
			if (template.isStaticOnly()) {
				collector.append(template.expression());
				return;
			}
			
			for (TemplateFragment fragment: template.fragments()) {
				if (fragment.isPlaceholder()) {
					String propertyName = fragment.getText();
					Property property = reason.entityType().getProperty(propertyName);
					Object propertyValue = property.get(reason);
					GenericModelType type = property.getType().getActualType(propertyValue);
					collector.appendProperty(reason, property, type, propertyValue);
				}
				else {
					collector.append(fragment.getText());
				}
			}
		}
	}


}

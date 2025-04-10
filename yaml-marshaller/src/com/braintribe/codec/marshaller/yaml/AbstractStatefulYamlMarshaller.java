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
package com.braintribe.codec.marshaller.yaml;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Consumer;

import com.braintribe.codec.marshaller.api.EntityVisitorOption;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.IdentityManagementMode;
import com.braintribe.codec.marshaller.api.IdentityManagementModeOption;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.api.ScalarsFirst;
import com.braintribe.codec.marshaller.api.TypeExplicitness;
import com.braintribe.codec.marshaller.api.TypeExplicitnessOption;
import com.braintribe.codec.marshaller.api.options.attributes.StabilizeOrderOption;
import com.braintribe.model.bvd.convert.Convert;
import com.braintribe.model.bvd.string.Concatenation;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.generic.value.Variable;

public abstract class AbstractStatefulYamlMarshaller {
	private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
	private static final char[][] ESCAPES_NORMAL = generateEscapes(false);
	private static final char[][] ESCAPES_WITH_PLACEHOLDERS = generateEscapes(true);
	protected final GmSerializationOptions options;
	protected final Writer writer;
	protected final Object rootValue;
	protected final Indent indent = new Indent(2);
	protected int anchorSequence;
	protected TypeExplicitness typeExplicitness;
	protected final Consumer<? super GenericEntity> entityVisitor;
	protected final IdentityManagementMode identityManagementMode;
	protected boolean stabilize;
	private boolean scalarsFirst;
	private Map<EntityType<?>, Iterable<Property>> orderedProperties;
	protected boolean placeholderSupport;
	protected final char[][] ESCAPES;

	public AbstractStatefulYamlMarshaller(GmSerializationOptions options, Writer writer, Object rootValue) {
		super();
		this.options = options;
		this.writer = writer;
		this.rootValue = rootValue;
		this.typeExplicitness = options.findOrNull(TypeExplicitnessOption.class);
		this.stabilize = options.findOrDefault(StabilizeOrderOption.class, false);
		if (this.typeExplicitness == null || this.typeExplicitness == TypeExplicitness.auto) {
			this.typeExplicitness = TypeExplicitness.entities;
		}
		this.entityVisitor = options.findOrNull(EntityVisitorOption.class);
		this.identityManagementMode = options.findAttribute(IdentityManagementModeOption.class).orElse(IdentityManagementMode.auto);
		this.scalarsFirst = options.findOrDefault(ScalarsFirst.class, false);
		
		if (this.scalarsFirst)
			this.orderedProperties = new IdentityHashMap<>();

		this.placeholderSupport = options.findOrDefault(PlaceholderSupport.class, false);
		
		this.ESCAPES = placeholderSupport? ESCAPES_WITH_PLACEHOLDERS: ESCAPES_NORMAL;
	}
	
	protected Iterable<Property> properties(EntityType<?> type) {
		if (scalarsFirst)
			return orderedProperties.computeIfAbsent(type, k -> {
				List<Property> originalProperties = k.getProperties();
				List<Property> properties = new ArrayList<>(originalProperties.size());

				for (Property property : originalProperties) {
				    if (property.getType().isScalar()) {
				        properties.add(property);
				    }
				}

				for (Property property : originalProperties) {
				    if (!property.getType().isScalar()) {
				        properties.add(property);
				    }
				}
				
				return properties;
			});
		else
			return type.getProperties();
	}

	protected void write(GenericModelType inferredType, GenericModelType type, Object value) throws IOException {
		write(inferredType, type, value, false);
	}

	protected void writeSpaceIfRequired(boolean required) throws IOException {
		if (required)
			writer.write(' ');
	}

	/**
	 * 
	 * @param inferredType
	 *            Expected type of value - e.g. property type or inferred root type
	 * @param type
	 *            Actual type of value
	 * @param value
	 *            Value to marshal
	 * @param isComplexPropertyValue
	 *            To prevent unnecessary newlines it is tried to render as little newlines as possible. However when a
	 *            property value of complex type is going to be marshaled for an entity, a newline (that otherwise would not
	 *            have been necessary) has to be enforced via this flag.
	 * @throws IOException
	 */
	protected void write(GenericModelType inferredType, GenericModelType type, Object value, boolean isComplexPropertyValue) throws IOException {
		if (value == null) {
			writeSpaceIfRequired(isComplexPropertyValue);
			writer.write("null");
			return;
		}

		switch (type.getTypeCode()) {
			case objectType:
				write(inferredType, type.getActualType(value), value, isComplexPropertyValue);
				break;

			// Strings
			case stringType:
				writeSpaceIfRequired(isComplexPropertyValue);
				writeString(value);
				break;

			// native literal types
			case booleanType:
			case integerType:
			case doubleType:
				writeSpaceIfRequired(isComplexPropertyValue);
				writer.write(value.toString());
				break;

			// straight custom types
			case longType:
			case floatType:
			case decimalType:
			case enumType:
				writeSpaceIfRequired(isComplexPropertyValue);
				writeSimpleCustomType(inferredType, type, value);
				break;

			case dateType:
				writeSpaceIfRequired(isComplexPropertyValue);
				writeDate((Date) value);
				break;

			case entityType:
				writeEntity(inferredType, (GenericEntity) value, isComplexPropertyValue);
				break;

			// collection types
			case listType:
				writeList((ListType) type, (List<?>) value, isComplexPropertyValue);
				break;
			case setType:
				writeSet((SetType) type, (Set<?>) value, isComplexPropertyValue);
				break;
			case mapType:
				writeMap((MapType) type, (Map<?, ?>) value, isComplexPropertyValue);
				break;

			default:
				break;

		}
	}

	protected void writeMap(MapType type, Map<?, ?> map, boolean isComplexPropertyValue) throws IOException {
		if (map.isEmpty()) {
			writeSpaceIfRequired(isComplexPropertyValue);
			writer.write("{}");
			return;
		}

		boolean forceNewline = isComplexPropertyValue;

		GenericModelType keyType = type.getKeyType();
		GenericModelType valueType = type.getValueType();

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (forceNewline) { // ensures that a newline is written when the collection has at least one entry and is the property of an entity
				writer.write('\n');
				indent.write(writer);
			}
			forceNewline = true;

			GenericModelType actualKeyType = keyType.isBase() ? keyType.getActualType(key) : keyType;

			if (actualKeyType.isScalar()) {
				write(keyType, keyType, key);
				writer.write(':');
				indent.pushIndent();
				write(valueType, valueType, value, true);
				indent.popIndent();
			} else {
				writer.write("? ");
				indent.pushIndent();
				write(keyType, actualKeyType, key);
				indent.popIndent();
				writer.write("\n");
				indent.write(writer);
				writer.write(':');
				indent.pushIndent();
				write(valueType, valueType, value, true);
				indent.popIndent();
			}

		}
	}

	protected void writeList(ListType type, List<?> list, boolean isComplexPropertyValue) throws IOException {
		writeLinearCollection(type, list, '-', isComplexPropertyValue, false);
	}

	protected void writeSet(SetType type, Set<?> set, boolean isComplexPropertyValue) throws IOException {
		writeSpaceIfRequired(isComplexPropertyValue);
		writer.write("!!set");
		writeLinearCollection(type, set, '?', true, true);
	}

	protected void writeLinearCollection(LinearCollectionType type, Collection<?> collection, char bullet, boolean isComplexPropertyValue,
			boolean introductionWritten) throws IOException {
		if (collection.isEmpty()) {
			writeSpaceIfRequired(isComplexPropertyValue);
			writer.write("[]");
			return;
		}

		boolean forceNewline = introductionWritten || isComplexPropertyValue;

		GenericModelType elementType = type.getCollectionElementType();
		for (Object element : collection) {
			if (forceNewline) {
				writer.write('\n');
				indent.write(writer);
			}
			forceNewline = true;
			writer.write(bullet);
			writer.write(' ');
			indent.pushIndent();
			write(elementType, elementType, element);
			indent.popIndent();
		}
	}

	// Formatter to be used for later replacement of the proprietary date formatting copied over from snakeyaml
	private static final DateTimeFormatter DATETIME_FORMATTER = new DateTimeFormatterBuilder().optionalStart()
			.appendPattern("yyyy-MM-dd['T'HH[:mm[:ss[.SSS]]]][Z]").optionalEnd().optionalStart().appendPattern("yyyyMMdd['T'HH[mm[ss[SSS]]]][Z]")
			.optionalEnd().parseDefaulting(ChronoField.HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
			.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
			.parseDefaulting(ChronoField.OFFSET_SECONDS, 0).toFormatter();

	protected void writeDate(Date value) throws IOException {
		// TODO: finde the correct pattern for jdk date handling to use it instead of manual date formatting copied over from
		// snakeyamls date represent
		// DATETIME_FORMATTER.formatTo(value.toInstant(), writer);

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(value);
		int years = calendar.get(Calendar.YEAR);
		int months = calendar.get(Calendar.MONTH) + 1; // 0..12
		int days = calendar.get(Calendar.DAY_OF_MONTH); // 1..31
		int hour24 = calendar.get(Calendar.HOUR_OF_DAY); // 0..24
		int minutes = calendar.get(Calendar.MINUTE); // 0..59
		int seconds = calendar.get(Calendar.SECOND); // 0..59
		int millis = calendar.get(Calendar.MILLISECOND);

		String yearsStr = String.valueOf(years);
		int pad = Math.max(4 - yearsStr.length(), 0);
		for (int p = 0; p < pad; p++) {
			writer.write('0');
		}
		writer.write(yearsStr);
		writer.write('-');
		if (months < 10) {
			writer.write('0');
		}
		writer.write(String.valueOf(months));
		writer.write('-');
		if (days < 10) {
			writer.write('0');
		}
		writer.write(String.valueOf(days));
		writer.write('T');
		if (hour24 < 10) {
			writer.write('0');
		}
		writer.write(String.valueOf(hour24));
		writer.write(':');
		if (minutes < 10) {
			writer.write('0');
		}
		writer.write(String.valueOf(minutes));
		writer.write(':');
		if (seconds < 10) {
			writer.write('0');
		}
		writer.write(String.valueOf(seconds));
		if (millis > 0) {
			if (millis < 10) {
				writer.write(".00");
			} else if (millis < 100) {
				writer.write(".0");
			} else {
				writer.write('.');
			}
			writer.write(String.valueOf(millis));
		}

		// Get the offset from GMT taking DST into account
		int gmtOffset = calendar.getTimeZone().getOffset(calendar.get(Calendar.ERA), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.MILLISECOND));
		if (gmtOffset == 0) {
			writer.write('Z');
		} else {
			if (gmtOffset < 0) {
				writer.write('-');
				gmtOffset *= -1;
			} else {
				writer.write('+');
			}
			int minutesOffset = gmtOffset / (60 * 1000);
			int hoursOffset = minutesOffset / 60;
			int partOfHour = minutesOffset % 60;

			if (hoursOffset < 10) {
				writer.write('0');
			}
			writer.write(String.valueOf(hoursOffset));
			writer.write(':');
			if (partOfHour < 10) {
				writer.write('0');
			}
			writer.write(String.valueOf(partOfHour));
		}
	}

	protected void writePlaceholder(ValueDescriptor vd) throws IOException {
		writer.write('"');
		writePlaceholderDirect(vd);
		writer.write('"');
	}
	
	protected void writePlaceholderDirect(ValueDescriptor vd) throws IOException {
		EntityType<? extends ValueDescriptor> type = vd.entityType();
		
		if (vd instanceof Convert) {
			Convert convert = (Convert)vd;
			writeOperand(convert.getOperand());
		}
		else if (type == Concatenation.T) {
			Concatenation concatenation = (Concatenation)vd;
			
			for (Object op: concatenation.getOperands()) {
				writeOperand(op);
			}
		}
		else if (type == Variable.T) {
			Variable var = (Variable)vd;
			writer.write("${");
			writer.write(var.getName());
			writer.write('}');
		}
	}
	
	protected void writeOperand(Object op) throws IOException {
		if (op instanceof String) {
			writeEscaped(writer, (String)op);
		}
		else if (op instanceof ValueDescriptor) {
			writePlaceholderDirect((ValueDescriptor)op);
		}
	}
	
	protected void writeString(Object s) throws IOException {
		writer.write('"');
		writeEscaped(writer, s.toString());
		writer.write('"');
	}

	protected void writeSimpleCustomType(GenericModelType inferredType, GenericModelType type, Object value) throws IOException {
		if (typeExplicitness == TypeExplicitness.always || (typeExplicitness != TypeExplicitness.never && inferredType != type)) {
			writer.write('!');
			writer.write(type.getTypeSignature());
			writer.write(' ');
		}

		writer.write(value.toString());
	}

	protected static void writeSpacer(int count, Writer writer) throws IOException {
		if (count > 0)
			writer.append(' ');
	}

	protected abstract void writeEntity(GenericModelType inferredType, GenericEntity entity, boolean isComplexPropertyValue) throws IOException;

	private static char[][] generateEscapes(boolean placeholderSupport) {
		char[][] ESCAPES = new char[128][];
		
		ESCAPES['"'] = "\\\"".toCharArray();
		ESCAPES['\\'] = "\\\\".toCharArray();
		ESCAPES['\t'] = "\\t".toCharArray();
		ESCAPES['\f'] = "\\f".toCharArray();
		ESCAPES['\n'] = "\\n".toCharArray();
		ESCAPES['\r'] = "\\r".toCharArray();
		if (placeholderSupport)
			ESCAPES['$'] = "$$".toCharArray();

		for (int i = 0; i < 32; i++) {
			if (ESCAPES[i] == null)
				ESCAPES[i] = ("\\u00" + HEX_CHARS[i >> 4] + HEX_CHARS[i & 0xF]).toCharArray();
		}
		
		return ESCAPES;
	}

	protected void writeEscaped(Writer writer, String string) throws IOException {
		int len = string.length();
		int s = 0;
		int i = 0;
		char esc[] = null;
		for (; i < len; i++) {
			char c = string.charAt(i);

			if (c < 128) {
				esc = ESCAPES[c];
				if (esc != null) {
					writer.write(string, s, i - s);
					writer.write(esc);
					s = i + 1;
				}
			}
		}
		if (i > s) {
			if (s == 0)
				writer.write(string);
			else
				writer.write(string, s, i - s);
		}
	}

	static class Indent {
		private int depth = 0;
		private final int indentAmount;
		private static final String[] indents = { " ", "  ", "    ", "        ", "                ", "                                ",
				"                                                                ", };

		public Indent(int indentAmount) {
			this.indentAmount = indentAmount;
		}

		public int getDepth() {
			return depth;
		}

		public void pushIndent() {
			depth++;
		}

		public void popIndent() {
			depth--;
		}

		public void write(Writer writer) throws IOException {
			int num = depth * indentAmount;

			int len = indents.length;

			for (int i = 0; num != 0 && i < len; i++, num >>= 1) {
				if ((num & 1) != 0) {
					writer.write(indents[i]);
				}
			}

			// write remains
			if (num > 0) {
				String s = indents[len - 1];
				for (int i = 0; i < num * 2; i++) {
					writer.write(s);
				}
			}
		}
	}

}
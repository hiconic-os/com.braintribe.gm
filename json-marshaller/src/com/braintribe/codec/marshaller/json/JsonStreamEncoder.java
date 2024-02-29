package com.braintribe.codec.marshaller.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.EntityRecurrenceDepth;
import com.braintribe.codec.marshaller.api.EntityVisitorOption;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.api.PropertySerializationTranslation;
import com.braintribe.codec.marshaller.api.StringifyNumbersOption;
import com.braintribe.codec.marshaller.api.TypeExplicitness;
import com.braintribe.codec.marshaller.api.TypeExplicitnessOption;
import com.braintribe.codec.marshaller.api.options.attributes.InferredRootTypeOption;
import com.braintribe.codec.marshaller.api.options.attributes.OutputPrettinessOption;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.utils.io.UnsynchronizedBufferedWriter;

/**
 * @author peter.gazdik
 */
public class JsonStreamEncoder {

	private final UnsynchronizedBufferedWriter writer;
	private final Map<GenericEntity, Integer> idByEntities = new IdentityHashMap<>();
	private final Set<GenericEntity> recursiveRecurrenceSet = Collections.newSetFromMap(new IdentityHashMap<>());
	private final DateCoding dateCoding;
	private final boolean useDirectPropertyAccess;
	private final boolean writeEmptyProperties;
	private boolean canSkipNonPolymorphicType;
	private boolean writeSimplifiedValues;
	private final TypeExplicitness typeExplicitness;
	private final boolean writeAbsenceProperties;
	private final boolean stringifyNumbers;
	private final Consumer<? super GenericEntity> entityVisitor;
	private final Function<Property, String> propertyNameSupplier;
	private final PrettinessSupport prettinessSupport;
	private final GenericModelType rootType;

	private int idSequence = 0;
	private Integer entityRecurrenceDepth = 0;
	private int currentRecurrenceDepth = 0;

	public JsonStreamEncoder(GmSerializationOptions options, Writer writer) {
		this.writer = new UnsynchronizedBufferedWriter(writer);
		this.dateCoding = JsonStreamMarshaller.dateTimeFormatterFromOptions(options);
		this.rootType = options.findOrDefault(InferredRootTypeOption.class, BaseType.INSTANCE);
		this.useDirectPropertyAccess = options.useDirectPropertyAccess();
		this.writeEmptyProperties = options.writeEmptyProperties();
		this.writeAbsenceProperties = options.writeAbsenceInformation();
		this.stringifyNumbers = options.findOrDefault(StringifyNumbersOption.class, Boolean.FALSE);
		this.prettinessSupport = resolvePrettinessSupport(options);
		this.entityRecurrenceDepth = options.findOrDefault(EntityRecurrenceDepth.class, 0);
		this.entityVisitor = options.findOrDefault(EntityVisitorOption.class, null);
		this.propertyNameSupplier = options.findOrDefault(PropertySerializationTranslation.class, null);
		this.typeExplicitness = options.findOrDefault(TypeExplicitnessOption.class, TypeExplicitness.auto);

		switch (typeExplicitness) {
			case always:
				break;
			case auto:
			case entities:
				canSkipNonPolymorphicType = false;
				writeSimplifiedValues = true;
				break;
			case never:
			case polymorphic:
				canSkipNonPolymorphicType = true;
				writeSimplifiedValues = true;
				break;
			default:
				break;
		}
	}

	private static PrettinessSupport resolvePrettinessSupport(GmSerializationOptions options) {
		switch (options.findOrDefault(OutputPrettinessOption.class, OutputPrettiness.none)) {
			case high:
				return new HighPrettinessSupport();
			case low:
				return new LowPrettinessSupport();
			case mid:
				return new MidPrettinessSupport();
			case none:
			default:
				return new NoPrettinessSupport();
		}
	}

	public void encode(Object value) {
		try {
			marshall(rootType, value, 0, false);
			writer.flush();

		} catch (MarshallException e) {
			throw e;
		} catch (Exception e) {
			throw new MarshallException("error while marshalling json", e);
		}
	}

	private static final char[] nullLiteral = "null".toCharArray();
	private static final char[] trueLiteral = "true".toCharArray();
	private static final char[] falseLiteral = "false".toCharArray();

	private static final char[] openTypedValue = "{\"value\":".toCharArray();
	private static final char[] openTypedQuotedValue = "{\"value\":\"".toCharArray();
	private static final char[] closeDouble = ", \"_type\":\"double\"}".toCharArray();
	private static final char[] closeFloat = ", \"_type\":\"float\"}".toCharArray();
	private static final char[] closeDate = "\", \"_type\":\"date\"}".toCharArray();
	private static final char[] closeDecimal = "\", \"_type\":\"decimal\"}".toCharArray();
	private static final char[] closeLong = "\", \"_type\":\"long\"}".toCharArray();
	private static final char[] midEnum = "\", \"_type\":\"".toCharArray();
	private static final char[] closeEnum = "\"}".toCharArray();
	private static final char[] emptyList = "[]".toCharArray();
	private static final char[] openSet = "{\"_type\": \"set\", \"value\":[".toCharArray();
	private static final char[] emptySet = "{\"_type\": \"set\", \"value\":[]}".toCharArray();
	// private static final char[] openMap = "{\"_type\": \"map\", \"value\":[".toCharArray();
	private static final char[] openFlatMap = "{\"_type\": \"flatmap\", \"value\":[".toCharArray();
	// private static final char[] emptyMap = "{\"_type\": \"map\", \"value\":[]}".toCharArray();
	private static final char[] emptyFlatMap = "{\"_type\": \"flatmap\", \"value\":[]}".toCharArray();
	private static final char[] closeTypedCollection = "]}".toCharArray();
	// private static final char[] openEntry = "{\"key\":".toCharArray();
	// private static final char[] midEntry = ", \"value\":".toCharArray();

	private void marshall(GenericModelType contextType, Object value, int indent, boolean isIdentifier) throws IOException {
		if (value == null) {
			writer.write(nullLiteral);
			return;
		}

		GenericModelType type = contextType;
		while (true) {
			switch (type.getTypeCode()) {
				// object type: retrieve the actual type and do another round of type detection here
				case objectType:
					GenericModelType actualType = type.getActualType(value);
					if (type.equals(actualType)) {
						throw new MarshallException("Object type should resolve to a concrete type.");
					}
					type = actualType;
					writeSimplifiedValues = false;
					if (isIdentifier && writeSimplifiedValues && type.isScalar()) {
						writeSimplifiedValues = true;
					} else if (!allowsTypeExplicitness()) {
						writeSimplifiedValues = true;
					}
					break;

				case booleanType:
					if ((Boolean) value)
						writer.write(trueLiteral);
					else
						writer.write(falseLiteral);
					return;
				case stringType:
					writer.write('"');
					writeEscaped(writer, (String) value);
					writer.write('"');
					return;
				case dateType:
					if (writeSimplifiedValues) {
						writer.write('"');
						writer.write(dateCoding.encode((Date) value));
						writer.write('"');
					} else {
						writer.write(openTypedQuotedValue);
						writer.write(dateCoding.encode((Date) value));
						writer.write(closeDate);
					}
					return;
				case integerType:
					writer.write(value.toString());
					return;
				case doubleType:
					if (writeSimplifiedValues) {
						writer.write(value.toString());
					} else {
						writer.write(openTypedValue);
						writer.write(value.toString());
						writer.write(closeDouble);
					}
					return;
				case floatType:
					if (writeSimplifiedValues) {
						writer.write(value.toString());
					} else {
						writer.write(openTypedValue);
						writer.write(value.toString());
						writer.write(closeFloat);
					}
					return;
				case longType:
					if (writeSimplifiedValues) {
						writeSimplifiedNumberType(value);
					} else {
						writer.write(openTypedQuotedValue);
						writer.write(value.toString());
						writer.write(closeLong);
					}
					return;
				case decimalType:
					if (writeSimplifiedValues) {
						writeSimplifiedNumberType(value);
					} else {
						writer.write(openTypedQuotedValue);
						writer.write(value.toString());
						writer.write(closeDecimal);
					}
					return;

				// custom types
				case entityType:
					marshallEntity((GenericEntity) value, indent, contextType);
					return;

				case enumType:
					if (writeSimplifiedValues) {
						writer.write('"');
						writer.write(value.toString());
						writer.write('"');
					} else {
						writer.write(openTypedQuotedValue);
						writer.write(value.toString());
						writer.write(midEnum);
						writer.write(type.getTypeSignature());
						writer.write(closeEnum);
					}
					return;

				// collections
				case listType: {
					Collection<?> collection = (Collection<?>) value;
					marshallCollection((ListType) type, collection, indent);
					return;
				}
				case setType: {
					Collection<?> collection = (Collection<?>) value;
					if (writeSimplifiedValues) {
						marshallCollection((SetType) type, collection, indent);
					} else {
						if (collection.isEmpty()) {
							writer.write(emptySet);
							return;
						}
						writer.write(openSet);
						int i = 0;
						int elementIndent = indent + 1;
						GenericModelType elementType = ((CollectionType) type).getCollectionElementType();
						for (Object e : collection) {
							if (i > 0)
								writer.write(',');
							prettinessSupport.writeLinefeed(writer, elementIndent);

							marshall(elementType, e, elementIndent, false);
							i++;
						}
						prettinessSupport.writeLinefeed(writer, indent);
						writer.write(closeTypedCollection);
					}
					return;
				}
				case mapType: {
					Map<?, ?> map = (Map<?, ?>) value;
					if (map.isEmpty()) {
						writer.write(emptyFlatMap);
						return;
					}

					int i = 0;
					int elementIndent = indent + 1;
					int subElementIndent = indent + 2;
					GenericModelType[] parameterization = ((CollectionType) type).getParameterization();
					GenericModelType keyType = parameterization[0];
					GenericModelType valueType = parameterization[1];
					boolean isStringKey = keyType == EssentialTypes.TYPE_STRING;
					boolean isEnumKey = keyType.isEnum();
					boolean writeSimpleFlatMap = !allowsTypeExplicitness() || isStringKey || (isEnumKey && writeSimplifiedValues);
					if (writeSimpleFlatMap) {
						writer.write("{");
					} else {
						writer.write(openFlatMap);
					}
					for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
						if (i > 0)
							writer.write(',');
						prettinessSupport.writeLinefeed(writer, elementIndent);
						marshall(keyType, entry.getKey(), subElementIndent, false);
						if (writeSimpleFlatMap) {
							writer.write(":");
						} else {
							writer.write(",");
						}
						marshall(valueType, entry.getValue(), subElementIndent, false);
						i++;
					}
					prettinessSupport.writeLinefeed(writer, indent);
					if (writeSimpleFlatMap) {
						writer.write("}".toCharArray());
					} else {
						writer.write(closeTypedCollection);
					}
					return;
				}
				default:
					break;
			}
		}

	}

	private void writeSimplifiedNumberType(Object value) throws IOException {
		if (stringifyNumbers) {
			writer.write('"');
			writer.write(value.toString());
			writer.write('"');
		} else {
			writer.write(value.toString());
		}
	}

	private void marshallCollection(CollectionType collectionType, Collection<?> collection, int indent) throws IOException {
		if (collection.isEmpty()) {
			writer.write(emptyList);
			return;
		}
		writer.write('[');
		int i = 0;
		int elementIndent = indent + 1;
		GenericModelType elementType = collectionType.getCollectionElementType();
		for (Object e : collection) {
			if (i > 0)
				writer.write(',');
			prettinessSupport.writeLinefeed(writer, elementIndent);
			marshall(elementType, e, elementIndent, false);
			i++;
		}
		prettinessSupport.writeLinefeed(writer, indent);
		writer.write(']');
	}

	private static final char[] openEntityRef = "{\"_ref\": \"".toCharArray();
	private static final char[] closeEntityRef = "\"}".toCharArray();
	private static final char[] openEntity = "{\"_type\": \"".toCharArray();
	private static final char[] openTypeFreeEntity = "{\"_id\": \"".toCharArray();
	private static final char[] openTypeFreeEntityNoId = "{".toCharArray();
	private static final char[] idPartEntity = "\", \"_id\": \"".toCharArray();
	private static final char[] openEntityFinish = "\"".toCharArray();
	private static final char[] midProperty = "\": ".toCharArray();
	private static final char[] openAbsentProperty = "\"?".toCharArray();

	private void marshallEntity(GenericEntity entity, int indent, GenericModelType contextType) throws IOException {
		if (this.entityRecurrenceDepth == 0) {
			marsallEntityWithZeroEntityRecurrenceDepth(entity, indent, contextType);
		} else {
			marsallEntityWithEntityRecurrenceDepth(entity, indent, contextType);
		}
	}

	private void marsallEntityWithEntityRecurrenceDepth(GenericEntity entity, int indent, GenericModelType contextType) {

		boolean isInRecurrence = this.currentRecurrenceDepth > 0;
		if (isInRecurrence || lookupId(entity) != null) {
			currentRecurrenceDepth++;

			try {
				_marshallEntity(entity, indent, contextType);
			} finally {
				currentRecurrenceDepth--;
			}
		} else {
			register(entity);
			_marshallEntity(entity, indent, contextType);
		}
	}

	private void _marshallEntity(GenericEntity entity, int indent, GenericModelType contextType) {
		boolean nonRecursiveVisit = recursiveRecurrenceSet.add(entity);

		try {
			boolean onlyScalars = !nonRecursiveVisit || isRecurrenceMax();

			EntityType<?> type = entity.entityType();

			boolean skipType = !allowsTypeExplicitness() || (canSkipNonPolymorphicType && contextType == entity.entityType());
			if (skipType) {
				writer.write(openTypeFreeEntityNoId);
			} else {
				writer.write(openEntity);
				// encode entity
				writer.write(type.getTypeSignature());
			}

			if (!skipType) {
				writer.write(openEntityFinish);
			}

			int propertyIndent = indent + 1;
			int i = 0;
			for (Property property : entity.entityType().getProperties()) {
				GenericModelType propertyType = property.getType();
				if (!propertyType.isScalar() && onlyScalars) {
					continue;
				}

				Object value = useDirectPropertyAccess ? property.getDirectUnsafe(entity) : property.get(entity);

				if (value == null) {
					AbsenceInformation absenceInformation = property.getAbsenceInformation(entity);

					if (absenceInformation != null) {
						if (writeAbsenceProperties) {
							writer.write(',');
							prettinessSupport.writeLinefeed(writer, propertyIndent);
							writer.write(openAbsentProperty);
							writer.write(propertyName(property));
							writer.write(midProperty);
							marshallEntity(absenceInformation, propertyIndent, AbsenceInformation.T);
							i++;
						}
						continue;
					} else {
						if (!writeEmptyProperties)
							continue;
					}
				} else {
					if (!writeEmptyProperties && propertyType.getTypeCode() != TypeCode.objectType && propertyType.isEmpty(value))
						continue;
				}

				if (!skipType && i == 0) {
					writer.write(',');
				} else if (i > 0) {
					writer.write(',');
				}

				prettinessSupport.writeLinefeed(writer, propertyIndent);
				writer.write('"');
				writer.write(propertyName(property));
				writer.write(midProperty);

				// GenericModelType actualType = GMF.getTypeReflection().getBaseType().getActualType(value);
				marshall(propertyType, value, propertyIndent, property.isIdentifier());
				i++;
			}

			if (i > 0)
				prettinessSupport.writeLinefeed(writer, indent);

			writer.write('}');
		} catch (MarshallException e) {
			throw e;
		} catch (Exception e) {
			throw new MarshallException("error while encoding entity", e);
		} finally {
			if (nonRecursiveVisit)
				recursiveRecurrenceSet.remove(entity);
		}
	}

	private boolean isRecurrenceMax() {
		if (this.entityRecurrenceDepth < 0) {
			return false;
		}
		return this.currentRecurrenceDepth >= this.entityRecurrenceDepth;
	}

	private void marsallEntityWithZeroEntityRecurrenceDepth(GenericEntity entity, int indent, GenericModelType contextType) throws IOException {
		int indentLimit = prettinessSupport.getMaxIndent() - 4;
		if (indent > indentLimit)
			indent = indentLimit;

		Integer refId = lookupId(entity);
		if (refId != null) {
			// encode reference
			writer.write(openEntityRef);
			writer.write(refId.toString());
			writer.write(closeEntityRef);
		} else {
			try {
				EntityType<?> type = entity.entityType();

				boolean skipType = !allowsTypeExplicitness() || (canSkipNonPolymorphicType && contextType == entity.entityType());

				if (skipType) {
					writer.write(openTypeFreeEntity);
				} else {
					writer.write(openEntity);
					// encode entity
					writer.write(type.getTypeSignature());
					writer.write(idPartEntity);
				}

				refId = register(entity);
				writer.write(refId.toString());

				writer.write(openEntityFinish);

				int propertyIndent = indent + 1;
				int i = 0;
				for (Property property : type.getProperties()) {

					GenericModelType propertyType = property.getType();

					Object value = useDirectPropertyAccess ? property.getDirectUnsafe(entity) : property.get(entity);

					if (value == null) {
						AbsenceInformation absenceInformation = property.getAbsenceInformation(entity);

						if (absenceInformation != null) {
							if (writeAbsenceProperties) {
								writer.write(',');
								prettinessSupport.writeLinefeed(writer, propertyIndent);
								writer.write(openAbsentProperty);
								writer.write(propertyName(property));
								writer.write(midProperty);
								marshallEntity(absenceInformation, propertyIndent, AbsenceInformation.T);
								i++;
							}
							continue;
						} else {
							if (!writeEmptyProperties)
								continue;
						}
					} else {
						if (!writeEmptyProperties && propertyType.getTypeCode() != TypeCode.objectType && propertyType.isEmpty(value))
							continue;
					}

					writer.write(',');
					prettinessSupport.writeLinefeed(writer, propertyIndent);
					writer.write('"');
					writer.write(propertyName(property));
					writer.write(midProperty);

					// GenericModelType actualType = GMF.getTypeReflection().getBaseType().getActualType(value);
					marshall(propertyType, value, propertyIndent, property.isIdentifier());
					i++;
				}

				if (i > 0)
					prettinessSupport.writeLinefeed(writer, indent);

				writer.write('}');
			} catch (MarshallException e) {
				throw e;
			} catch (Exception e) {
				throw new MarshallException("error while encoding entity", e);
			}
		}
	}

	private boolean allowsTypeExplicitness() {
		return typeExplicitness != TypeExplicitness.never;
	}

	private Integer register(GenericEntity entity) {
		return idByEntities.computeIfAbsent(entity, e -> {
			if (entityVisitor != null)
				entityVisitor.accept(e);
			return idSequence++;
		});
	}

	private Integer lookupId(GenericEntity entity) {
		return idByEntities.get(entity);
	}

	private String propertyName(Property property) {
		return propertyNameSupplier != null ? propertyNameSupplier.apply(property) : property.getName();
	}

	private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
	private static final char[][] ESCAPES = new char[128][];

	static {
		ESCAPES['"'] = "\\\"".toCharArray();
		ESCAPES['\\'] = "\\\\".toCharArray();
		ESCAPES['\t'] = "\\t".toCharArray();
		ESCAPES['\f'] = "\\f".toCharArray();
		ESCAPES['\n'] = "\\n".toCharArray();
		ESCAPES['\r'] = "\\r".toCharArray();

		for (int i = 0; i < 32; i++) {
			if (ESCAPES[i] == null)
				ESCAPES[i] = ("\\u00" + HEX_CHARS[i >> 4] + HEX_CHARS[i & 0xF]).toCharArray();
		}
	}

	private static void writeEscaped(Writer writer, String string) throws IOException {
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
}

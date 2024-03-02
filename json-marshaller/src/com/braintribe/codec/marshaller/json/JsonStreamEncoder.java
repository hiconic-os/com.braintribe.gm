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
import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.BooleanType;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.DateType;
import com.braintribe.model.generic.reflection.DecimalType;
import com.braintribe.model.generic.reflection.DoubleType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.FloatType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.IntegerType;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.generic.reflection.LongType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.generic.reflection.StringType;
import com.braintribe.model.generic.reflection.TypeCode;
import com.braintribe.utils.io.UnsynchronizedBufferedWriter;

/**
 * @author peter.gazdik
 */
public class JsonStreamEncoder {

	private final UnsynchronizedBufferedWriter writer;

	private final Map<GenericEntity, Integer> idByEntities = new IdentityHashMap<>();
	private final Set<GenericEntity> recursiveRecurrenceSet = Collections.newSetFromMap(new IdentityHashMap<>());

	private final Map<EntityType<?>, EntityTypeInfo> entityTypeInfos = new IdentityHashMap<>();

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
	private final int entityRecurrenceDepth;

	private int indent = 0;
	private int idSequence = 0;
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

		initTypeExplicitness();
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

	private void initTypeExplicitness() {
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

	public void encode(Object value) {
		try {
			marshall(rootType, value, getTypeEncoder(rootType), false);
			writer.flush();

		} catch (MarshallException e) {
			throw e;
		} catch (Exception e) {
			throw new MarshallException("error while marshalling json", e);
		}
	}

	private final BaseTypeEncoder baseTypeEncoder = new BaseTypeEncoder();
	private final BooleanTypeEncoder booleanTypeEncoder = new BooleanTypeEncoder();
	private final StringTypeEncoder stringTypeEncoder = new StringTypeEncoder();
	private final DateTypeEncoder dateTypeEncoder = new DateTypeEncoder();
	private final IntegerTypeEncoder integerTypeEncoder = new IntegerTypeEncoder();
	private final DoubleTypeEncoder doubleTypeEncoder = new DoubleTypeEncoder();
	private final FloatTypeEncoder floatTypeEncoder = new FloatTypeEncoder();
	private final LongTypeEncoder longTypeEncoder = new LongTypeEncoder();
	private final DecimalTypeEncoder decimalTypeEncoder = new DecimalTypeEncoder();
	private final EntityTypeEncoder entityTypeEncoder = new EntityTypeEncoder();
	private final EnumTypeEncoder enumTypeEncoder = new EnumTypeEncoder();
	private final ListTypeEncoder listTypeEncoder = new ListTypeEncoder();
	private final SetTypeEncoder setTypeEncoder = new SetTypeEncoder();
	private final MapTypeEncoder mapTypeEncoder = new MapTypeEncoder();

	abstract class TypeEncoder<T extends GenericModelType> {
		public abstract void encode(GenericModelType ctxType, T superType, Object value, boolean simp, boolean isId) throws IOException;
	}

	class BaseTypeEncoder extends TypeEncoder<BaseType> {
		@Override
		public void encode(GenericModelType ctxType, BaseType superType, Object value, boolean simp, boolean isId) throws IOException {
			GenericModelType actualType = ctxType.getActualType(value);
			if (actualType == null || actualType == ctxType)
				throw new MarshallException("Cannot marshall value " + value + " as its type was resolved as " + actualType);

			boolean simpValues = false;
			if (isId && simp && actualType.isScalar())
				simpValues = true;
			else if (!allowsTypeExplicitness())
				simpValues = true;

			TypeEncoder<GenericModelType> typeEncoder = (TypeEncoder<GenericModelType>) getTypeEncoder(actualType);
			typeEncoder.encode(ctxType, actualType, value, simpValues, isId);
		}
	}

	class BooleanTypeEncoder extends TypeEncoder<BooleanType> {
		@Override
		public void encode(GenericModelType ctxType, BooleanType superType, Object value, boolean simp, boolean isId) throws IOException {
			if ((Boolean) value)
				writer.write(trueLiteral);
			else
				writer.write(falseLiteral);
		}
	}

	class StringTypeEncoder extends TypeEncoder<StringType> {
		@Override
		public void encode(GenericModelType ctxType, StringType superType, Object value, boolean simp, boolean isId) throws IOException {
			writer.write('"');
			writeEscaped(writer, (String) value);
			writer.write('"');
		}
	}

	class DateTypeEncoder extends TypeEncoder<DateType> {
		@Override
		public void encode(GenericModelType ctxType, DateType superType, Object value, boolean simp, boolean isId) throws IOException {
			if (simp) {
				writer.write('"');
				dateCoding.encodeTo((Date) value, writer);
				writer.write('"');
			} else {
				writer.write(openTypedQuotedValue);
				dateCoding.encodeTo((Date) value, writer);
				writer.write(closeDate);
			}
		}
	}

	class IntegerTypeEncoder extends TypeEncoder<IntegerType> {
		@Override
		public void encode(GenericModelType ctxType, IntegerType superType, Object value, boolean simp, boolean isId) throws IOException {
			writer.write(value.toString());
		}
	}

	class DoubleTypeEncoder extends TypeEncoder<DoubleType> {
		@Override
		public void encode(GenericModelType ctxType, DoubleType superType, Object value, boolean simp, boolean isId) throws IOException {
			if (simp)
				writer.write(value.toString());
			else
				writeValue(openTypedValue, value, closeDouble);
		}
	}

	class FloatTypeEncoder extends TypeEncoder<FloatType> {
		@Override
		public void encode(GenericModelType ctxType, FloatType superType, Object value, boolean simp, boolean isId) throws IOException {
			if (simp)
				writer.write(value.toString());
			else
				writeValue(openTypedValue, value, closeFloat);
		}
	}

	class LongTypeEncoder extends TypeEncoder<LongType> {
		@Override
		public void encode(GenericModelType ctxType, LongType superType, Object value, boolean simp, boolean isId) throws IOException {
			if (simp)
				writeSimplifiedNumberType(value);
			else
				writeValue(openTypedQuotedValue, value, closeLong);
		}
	}

	class DecimalTypeEncoder extends TypeEncoder<DecimalType> {
		@Override
		public void encode(GenericModelType ctxType, DecimalType superType, Object value, boolean simp, boolean isId) throws IOException {
			if (simp)
				writeSimplifiedNumberType(value);
			else
				writeValue(openTypedQuotedValue, value, closeDecimal);
		}
	}

	private void writeValue(char[] open, Object value, char[] close) throws IOException {
		writer.write(open);
		writer.write(value.toString());
		writer.write(close);
	}

	class EntityTypeEncoder extends TypeEncoder<EntityType<?>> {
		@Override
		public void encode(GenericModelType ctxType, EntityType<?> superType, Object value, boolean simp, boolean isId) throws IOException {
			GenericEntity entity = (GenericEntity) value;

			EntityTypeInfo entityTypeInfo = acquireEntityTypeInfo(entity.entityType());

			if (entityRecurrenceDepth == 0) {
				marsallEntityWithZeroRecurrenceDepth(entity, entityTypeInfo, ctxType);
			} else {
				marsallEntityWithRecurrenceDepth(entity, entityTypeInfo, ctxType);
			}
		}
	}

	class EnumTypeEncoder extends TypeEncoder<EnumType> {
		@Override
		public void encode(GenericModelType ctxType, EnumType superType, Object value, boolean simp, boolean isId) throws IOException {
			if (simp) {
				writer.write('"');
				writer.write(value.toString());
				writer.write('"');
			} else {
				writer.write(openTypedQuotedValue);
				writer.write(value.toString());
				writer.write(midEnum);
				writer.write(superType.getTypeSignature());
				writer.write(closeEnum);
			}
		}
	}

	class ListTypeEncoder extends TypeEncoder<ListType> {
		@Override
		public void encode(GenericModelType ctxType, ListType superType, Object value, boolean simp, boolean isId) throws IOException {
			Collection<?> collection = (Collection<?>) value;
			marshallCollection(superType, collection);
		}
	}

	class SetTypeEncoder extends TypeEncoder<SetType> {
		@Override
		public void encode(GenericModelType ctxType, SetType superType, Object value, boolean simp, boolean isId) throws IOException {
			Collection<?> collection = (Collection<?>) value;
			if (simp) {
				marshallCollection(superType, collection);
			} else {
				if (collection.isEmpty()) {
					writer.write(emptySet);
					return;
				}
				writer.write(openSet);
				int i = 0;
				GenericModelType elementType = superType.getCollectionElementType();
				TypeEncoder<?> elementTypeEncoder = getTypeEncoder(elementType);
				indent++;
				for (Object e : collection) {
					if (i > 0)
						writer.write(',');
					prettinessSupport.writeLinefeed(writer, indent);

					marshall(elementType, e, elementTypeEncoder, false);
					i++;
				}
				indent--;
				prettinessSupport.writeLinefeed(writer, indent);
				writer.write(closeTypedCollection);
			}
		}
	}

	class MapTypeEncoder extends TypeEncoder<MapType> {
		@Override
		public void encode(GenericModelType ctxType, MapType superType, Object value, boolean simp, boolean isId) throws IOException {
			Map<?, ?> map = (Map<?, ?>) value;
			if (map.isEmpty()) {
				writer.write(emptyFlatMap);
				return;
			}

			int i = 0;

			GenericModelType[] parameterization = superType.getParameterization();
			GenericModelType keyType = parameterization[0];
			GenericModelType valueType = parameterization[1];
			TypeEncoder<?> keyTypeEncoder = getTypeEncoder(keyType);
			TypeEncoder<?> valueTypeEncoder = getTypeEncoder(valueType);

			boolean isStringKey = keyType == EssentialTypes.TYPE_STRING;
			boolean isEnumKey = keyType.isEnum();
			boolean writeSimpleFlatMap = !allowsTypeExplicitness() || isStringKey || (isEnumKey && simp);
			if (writeSimpleFlatMap) {
				writer.write('{');
			} else {
				writer.write(openFlatMap);
			}

			int elementIndent = indent + 1;
			indent += 2;
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
				if (i > 0)
					writer.write(',');
				prettinessSupport.writeLinefeed(writer, elementIndent);
				marshall(keyType, entry.getKey(), keyTypeEncoder, false);
				if (writeSimpleFlatMap) {
					writer.write(':');
				} else {
					writer.write(',');
				}
				marshall(valueType, entry.getValue(), valueTypeEncoder, false);
				i++;
			}
			indent -= 2;
			prettinessSupport.writeLinefeed(writer, indent);
			if (writeSimpleFlatMap) {
				writer.write('}');
			} else {
				writer.write(closeTypedCollection);
			}
		}
	}

	class EntityTypeInfo {
		public final EntityType<?> entityType;
		public final char[] typeSignatureChars;
		public final PropertyInfo[] propertyInfos;

		public EntityTypeInfo(EntityType<?> entityType) {
			this.entityType = entityType;
			this.typeSignatureChars = entityType.getTypeSignature().toCharArray();
			this.propertyInfos = entityType.getProperties().stream() //
					.map(PropertyInfo::new) //
					.toArray(PropertyInfo[]::new);
		}
	}

	class PropertyInfo {
		public final Property property;
		public final char[] propertyNameChars;
		public final TypeEncoder<?> typeEncoder;

		public PropertyInfo(Property property) {
			this.property = property;
			this.propertyNameChars = propertyName(property).toCharArray();
			this.typeEncoder = getTypeEncoder(property.getType());
		}

		private String propertyName(Property property) {
			return propertyNameSupplier != null ? propertyNameSupplier.apply(property) : property.getName();
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

	private EntityTypeInfo acquireEntityTypeInfo(EntityType<?> entityType) {
		return entityTypeInfos.computeIfAbsent(entityType, EntityTypeInfo::new);
	}

	private void marshall(GenericModelType ctxType, Object value, TypeEncoder<?> typeEncoder, boolean isId) throws IOException {
		if (value == null) {
			writer.write(nullLiteral);
			return;
		}

		TypeEncoder<GenericModelType> encoder = (TypeEncoder<GenericModelType>) typeEncoder;
		encoder.encode(ctxType, ctxType, value, writeSimplifiedValues, isId);
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

	private void marshallCollection(CollectionType collectionType, Collection<?> collection) throws IOException {
		if (collection.isEmpty()) {
			writer.write(emptyList);
			return;
		}
		writer.write('[');
		int i = 0;
		GenericModelType elementType = collectionType.getCollectionElementType();
		TypeEncoder<?> elementTypeEncoder = getTypeEncoder(elementType);
		indent++;
		for (Object e : collection) {
			if (i > 0)
				writer.write(',');
			prettinessSupport.writeLinefeed(writer, indent);
			marshall(elementType, e, elementTypeEncoder, false);
			i++;
		}
		indent--;
		prettinessSupport.writeLinefeed(writer, indent);
		writer.write(']');
	}

	private TypeEncoder<?> getTypeEncoder(GenericModelType ctxType) {
		switch (ctxType.getTypeCode()) {
			case booleanType:
				return booleanTypeEncoder;
			case dateType:
				return dateTypeEncoder;
			case decimalType:
				return decimalTypeEncoder;
			case doubleType:
				return doubleTypeEncoder;
			case entityType:
				return entityTypeEncoder;
			case enumType:
				return enumTypeEncoder;
			case floatType:
				return floatTypeEncoder;
			case integerType:
				return integerTypeEncoder;
			case listType:
				return listTypeEncoder;
			case longType:
				return longTypeEncoder;
			case mapType:
				return mapTypeEncoder;
			case objectType:
				return baseTypeEncoder;
			case setType:
				return setTypeEncoder;
			case stringType:
				return stringTypeEncoder;
			default:
				throw new UnknownEnumException(ctxType.getTypeCode());
		}
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

	private void marsallEntityWithRecurrenceDepth(GenericEntity entity, EntityTypeInfo typeInfo, GenericModelType ctxType) {
		boolean isInRecurrence = currentRecurrenceDepth > 0;
		if (isInRecurrence || lookupId(entity) != null) {

			currentRecurrenceDepth++;
			try {
				_marshallEntity(entity, typeInfo, ctxType);
			} finally {
				currentRecurrenceDepth--;
			}

		} else {
			register(entity);
			_marshallEntity(entity, typeInfo, ctxType);
		}
	}

	private void _marshallEntity(GenericEntity entity, EntityTypeInfo typeInfo, GenericModelType ctxType) {
		boolean nonRecursiveVisit = recursiveRecurrenceSet.add(entity);

		try {
			boolean onlyScalars = !nonRecursiveVisit || isRecurrenceMax();

			boolean skipType = canSkipType(typeInfo, ctxType);
			if (skipType) {
				writer.write(openTypeFreeEntityNoId);
			} else {
				writer.write(openEntity);
				writer.write(typeInfo.typeSignatureChars);
				writer.write(openEntityFinish);
			}

			boolean wroteProperty = false;
			indent++;
			for (int i = 0, len = typeInfo.propertyInfos.length; i < len; i++) {
				PropertyInfo propertyInfo = typeInfo.propertyInfos[i];
				Property property = propertyInfo.property;

				GenericModelType propertyType = property.getType();
				if (!propertyType.isScalar() && onlyScalars) {
					continue;
				}

				Object value = getProperty(entity, property);

				if (value == null) {

					AbsenceInformation absenceInformation = property.getAbsenceInformation(entity);
					if (absenceInformation == null) {
						if (!writeEmptyProperties)
							continue;
					} else {
						if (writeAbsenceProperties) {
							if (!skipType || wroteProperty)
								writer.write(',');
							prettinessSupport.writeLinefeed(writer, indent);
							writer.write(openAbsentProperty);
							writer.write(propertyInfo.propertyNameChars);
							writer.write(midProperty);
							marsallEntityWithRecurrenceDepth(absenceInformation, absenceInfoTypeInfo(), AbsenceInformation.T);
							wroteProperty = true;
						}
						continue;
					}

				} else {
					if (!writeEmptyProperties && propertyType.getTypeCode() != TypeCode.objectType && propertyType.isEmpty(value))
						continue;
				}

				if (!skipType || wroteProperty)
					writer.write(',');

				prettinessSupport.writeLinefeed(writer, indent);
				writer.write('"');
				writer.write(propertyInfo.propertyNameChars);
				writer.write(midProperty);

				marshall(propertyType, value, propertyInfo.typeEncoder, property.isIdentifier());
				wroteProperty = true;
			}
			indent--;

			if (wroteProperty)
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
		if (entityRecurrenceDepth < 0) {
			return false;
		}
		return currentRecurrenceDepth >= entityRecurrenceDepth;
	}

	private void marsallEntityWithZeroRecurrenceDepth(GenericEntity entity, EntityTypeInfo typeInfo, GenericModelType ctxType) throws IOException {
		int indentLimit = prettinessSupport.getMaxIndent() - 4;
		if (indent > indentLimit)
			indent = indentLimit;

		Integer refId = lookupId(entity);
		if (refId != null) {
			writer.write(openEntityRef);
			writer.write(refId.toString());
			writer.write(closeEntityRef);
			return;
		}

		try {
			boolean skipType = canSkipType(typeInfo, ctxType);
			if (skipType) {
				writer.write(openTypeFreeEntity);
			} else {
				writer.write(openEntity);
				writer.write(typeInfo.typeSignatureChars);
				writer.write(idPartEntity);
			}

			refId = register(entity);
			writer.write(refId.toString());

			writer.write(openEntityFinish);

			boolean wroteProperty = false;
			indent++;
			for (int i = 0, len = typeInfo.propertyInfos.length; i < len; i++) {
				PropertyInfo propertyInfo = typeInfo.propertyInfos[i];
				Property property = propertyInfo.property;

				GenericModelType propertyType = property.getType();

				Object value = getProperty(entity, property);

				if (value == null) {

					AbsenceInformation absenceInformation = property.getAbsenceInformation(entity);
					if (absenceInformation == null) {
						if (!writeEmptyProperties)
							continue;

					} else {
						if (writeAbsenceProperties) {
							writer.write(',');
							prettinessSupport.writeLinefeed(writer, indent);
							writer.write(openAbsentProperty);
							writer.write(propertyInfo.propertyNameChars);
							writer.write(midProperty);
							marsallEntityWithZeroRecurrenceDepth(absenceInformation, absenceInfoTypeInfo(), AbsenceInformation.T);
							wroteProperty = true;
						}
						continue;
					}

				} else {
					if (!writeEmptyProperties && propertyType.getTypeCode() != TypeCode.objectType && propertyType.isEmpty(value))
						continue;
				}

				writer.write(',');
				prettinessSupport.writeLinefeed(writer, indent);
				writer.write('"');
				writer.write(propertyInfo.propertyNameChars);
				writer.write(midProperty);

				marshall(propertyType, value, propertyInfo.typeEncoder, property.isIdentifier());
				wroteProperty = true;
			}
			indent--;

			if (wroteProperty)
				prettinessSupport.writeLinefeed(writer, indent);

			writer.write('}');

		} catch (MarshallException e) {
			throw e;
		} catch (Exception e) {
			throw new MarshallException("error while encoding entity", e);
		}
	}

	private boolean canSkipType(EntityTypeInfo typeInfo, GenericModelType ctxType) {
		return !allowsTypeExplicitness() || (canSkipNonPolymorphicType && ctxType == typeInfo.entityType);
	}

	private Object getProperty(GenericEntity entity, Property property) {
		return useDirectPropertyAccess ? property.getDirectUnsafe(entity) : property.get(entity);
	}

	private EntityTypeInfo absenceInfoTypeInfo;

	private EntityTypeInfo absenceInfoTypeInfo() {
		if (absenceInfoTypeInfo == null)
			absenceInfoTypeInfo = acquireEntityTypeInfo(AbsenceInformation.T);
		return absenceInfoTypeInfo;
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

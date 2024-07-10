package com.braintribe.codec.marshaller.json.buffer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.CmdResolverOption;
import com.braintribe.codec.marshaller.api.EntityVisitorOption;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.IdTypeSupplier;
import com.braintribe.codec.marshaller.api.IdentityManagementMode;
import com.braintribe.codec.marshaller.api.IdentityManagementModeOption;
import com.braintribe.codec.marshaller.api.PropertyDeserializationTranslation;
import com.braintribe.codec.marshaller.api.PropertyTypeInferenceOverride;
import com.braintribe.codec.marshaller.api.options.attributes.InferredRootTypeOption;
import com.braintribe.codec.marshaller.json.DateCoding;
import com.braintribe.codec.marshaller.json.JsonModelDataMappingException;
import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.session.GmSession;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.oracle.EntityTypeOracle;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JsonModelDataParser implements ConversionContext {
	private final AbsenceInformation absenceInformationForMissingProperties = GMF.absenceInformation();
	private JsonParser parser;
	private Deque<JsonComplexValue> valueStack = new ArrayDeque<>();
	private JsonName currentName = null;
	private GenericModelType inferredRootType;
	private DateCoding dateCoding;
	private boolean enhanced;
	private boolean snakeCaseProperties;
	private GmSession session;
	private boolean assignAbsenceInformation;
	private IdentityManagementMode identityManagementMode;
	private Consumer<? super GenericEntity> entityVisitor;
	private BiFunction<EntityType<?>, Property, GenericModelType> propertyTypeInferenceOverride;
	private Function<String, GenericModelType> idTypeSupplier;
	private BiFunction<EntityType<?>, String, Property> propertySupplier;
	private boolean isPropertyLenient;
	private Map<String, GenericEntity> entitiesByRefId = new HashMap<>();
	private Map<EntityType<?>, Map<String, EntityType<?>>> typeSpecificProperties = new HashMap<>();
	private CmdResolver cmdResolver;
	private Map<Pair<EntityType<?>, Object>, GenericEntity> entitiesById = new HashMap<>();
	private Map<String, GenericEntity> entitiesByGlobalId = new HashMap<>();
	
	public JsonModelDataParser(JsonParser parser, GmDeserializationOptions options, boolean enhanced, boolean snakeCaseProperties) {
		this.parser = parser;
		this.inferredRootType = options.getInferredRootType();
		
		this.dateCoding = DateCoding.fromOptions(options);
		this.parser = parser;
		this.enhanced = enhanced;
		this.snakeCaseProperties = snakeCaseProperties;
		this.session = options.getSession();
		this.assignAbsenceInformation = options.getAbsentifyMissingProperties();
		this.inferredRootType = options.findAttribute(InferredRootTypeOption.class).orElse(BaseType.INSTANCE);

		this.identityManagementMode = options.findOrDefault(IdentityManagementModeOption.class, IdentityManagementMode.auto);
		this.entityVisitor = options.findOrNull(EntityVisitorOption.class);
		this.propertyTypeInferenceOverride = options.findOrNull(PropertyTypeInferenceOverride.class);
		this.idTypeSupplier = options.findAttribute(IdTypeSupplier.class).orElse(null);
		this.propertySupplier = options.findAttribute(PropertyDeserializationTranslation.class).orElse(null);
		this.isPropertyLenient = options.getDecodingLenience() != null && options.getDecodingLenience().isPropertyLenient();
		this.cmdResolver = options.findOrNull(CmdResolverOption.class);
	}
	
	private JsonSpan getTokenSpan() {
		return new JsonSpan(parser.currentTokenLocation(), parser.currentLocation());
	}
	
	public Maybe<Object> parse() throws IOException {
		try {
			return Maybe.complete(parseInternal());
		}
		catch (MappingError mappingError) {
			return Reasons.build(ParseError.T).text("Error while mapping json to modeled data").cause(mappingError.getReason()).toMaybe();
		}
		catch (JsonParseException e) {
			JsonLocation l1 = parser.currentTokenLocation();
			JsonLocation l2 = e.getLocation();
			String msg = e.getOriginalMessage() + " " + new JsonSpan(l1, l2);
			return Reasons.build(ParseError.T).text(msg).toMaybe();
		}
	}
	
	private Object parseInternal() throws IOException {
		JsonToken token = null;
		
		JsonRootValue rootValue = new JsonRootValue(this, inferredRootType, parser.currentLocation());
		push(rootValue);
		
		while ((token = parser.nextToken()) != null) {
			
			if (token == JsonToken.FIELD_NAME) {
				currentName = new JsonName(this, parser.currentName(), parser.currentTokenLocation(), parser.currentLocation());
				continue;
			}
			
			switch (token) {
				case START_OBJECT: push(new JsonObjectValue(this, parser.currentTokenLocation())); break;
				case START_ARRAY: push(new JsonArrayValue(this, parser.currentTokenLocation())); break;
				case END_ARRAY: pop(); break;
				case END_OBJECT: pop(); break;
				
				case VALUE_NULL: addScalarValue(EssentialTypes.TYPE_OBJECT, null); break;
				case VALUE_TRUE: addScalarValue(EssentialTypes.TYPE_BOOLEAN, true); break;
				case VALUE_FALSE: addScalarValue(EssentialTypes.TYPE_BOOLEAN, false); break;
				case VALUE_STRING: addScalarValue(EssentialTypes.TYPE_STRING, parser.getValueAsString()); break;
				case VALUE_NUMBER_FLOAT: addNumberValue(); break;
				case VALUE_NUMBER_INT: addNumberValue(); break;
				
				default: 
					throw new JsonModelDataMappingException("unexpected token type: " + token + " " + getTokenSpan());
			}
			
			// reset name after any value has been given or started for it
			currentName = null;
		}
		
		JsonValue jsonValue = pop();
		
		try {
			return jsonValue.as(inferredRootType);
		}
		catch (ConversionError e) {
			String msg = "Invalid root value " + jsonValue.getErrorLocation();
			InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text(msg).cause(e.getReason()).toReason();
			throw new MappingError(invalidArgument);
		}
	}
	
	private void push(JsonComplexValue complexValue) {
		addValue(complexValue);
		valueStack.push(complexValue);
	}
	
	private JsonValue pop() {
		return valueStack.pop();
	}
	
	private void addNumberValue() throws IOException {
		switch (parser.getNumberType()) {
			case BIG_DECIMAL: addScalarValue(EssentialTypes.TYPE_DECIMAL, parser.getDecimalValue()); break;
			case BIG_INTEGER: addScalarValue(EssentialTypes.TYPE_DECIMAL, parser.getDecimalValue()); break;
			case DOUBLE: addScalarValue(EssentialTypes.TYPE_DOUBLE, parser.getDoubleValue()); break;
			case FLOAT: addScalarValue(EssentialTypes.TYPE_FLOAT, parser.getFloatValue()); break;
			case INT: addScalarValue(EssentialTypes.TYPE_INTEGER, parser.getIntValue()); break;
			case LONG: addScalarValue(EssentialTypes.TYPE_LONG, parser.getLongValue()); break;
		}
	}
	
	private void addScalarValue(GenericModelType type, Object value) {
		addValue(new JsonScalarValue(this, type, value, parser.currentTokenLocation(), parser.currentLocation()));
	}
	
	private void addValue(JsonValue value) {
		JsonComplexValue peek = valueStack.peek();
		if (peek != null)
			peek.addValue(currentName, value);
	}

	@Override
	public DateCoding getDateCoding() {
		return dateCoding;
	}

	@Override
	public GenericEntity resolveReference(String ref) {
		return entitiesByRefId.get(ref);
	}
	
	@Override
	public GenericEntity resolveEntityById(EntityType<?> concreteType, Object entityId) {
		return entitiesById.get(Pair.of(concreteType, entityId));
	}
	
	@Override
	public GenericEntity resolveEntityByGlobalId(String entityGlobalId) {
		return entitiesByGlobalId.get(entityGlobalId);
	}

	@Override
	public GenericEntity createEntity(EntityType<?> entityType) {
		GenericEntity entity = (session != null ? session.createRaw(entityType) : enhanced ? entityType.createRaw() : entityType.createPlainRaw());

		if (assignAbsenceInformation) {
			entityType.getProperties().forEach(p -> p.setAbsenceInformation(entity, absenceInformationForMissingProperties));
		}

		if (entityVisitor != null)
			entityVisitor.accept(entity);

		return entity;
	}

	@Override
	public boolean registerEntity(GenericEntity entity, String id) {
		return entitiesByRefId.put(id, entity) == null;
	}
	
	@Override
	public void registerEntityByGlobalId(GenericEntity entity, String entityGlobalId) {
		entitiesByGlobalId.put(entityGlobalId, entity);
	}
	
	@Override
	public void registerEntityById(GenericEntity entity, Object id) {
		entitiesById.put(Pair.of(entity.entityType(), id), entity);
	}
	
	@Override
	public BiFunction<EntityType<?>, String, Property> getPropertySupplier() {
		return propertySupplier;
	}

	@Override
	public BiFunction<EntityType<?>, Property, GenericModelType> getPropertyTypeInferenceOverride() {
		return propertyTypeInferenceOverride;
	}

	@Override
	public boolean isPropertyLenient() {
		return isPropertyLenient;
	}

	@Override
	public boolean snakeCaseProperties() {
		return snakeCaseProperties;
	}

	@Override
	public Function<String, GenericModelType> idTypeSupplier() {
		return idTypeSupplier;
	}

	@Override
	public SpecialField detectSpecialField(String n) {
		SpecialField specialField = SpecialField.find(n);
		
		if (specialField == null)
			return null;
		
		if (identityManagementMode == IdentityManagementMode.auto) {
			IdentityManagementMode inferredMode = specialField.inferredIdentityManagementMode;
			if (inferredMode != null)
				identityManagementMode = inferredMode;
		}
		
		return specialField;
	}
	
	@Override
	public IdentityManagementMode identityManagedMode() {
		return identityManagementMode;
	}
	
	@Override
	public Map<String, EntityType<?>> getTypeSpecificProperties(EntityType<?> entityType) {
		return typeSpecificProperties.computeIfAbsent(entityType, this::buildTypeSpecificProperties);
	}
	
	private Map<String, EntityType<?>> buildTypeSpecificProperties(EntityType<?> entityType) {
		if (cmdResolver == null)
			return Collections.emptyMap();
		
		Set<EntityTypeOracle> subTypeOracles = cmdResolver.getModelOracle().findEntityTypeOracle(entityType).getSubTypes().transitive().onlyInstantiable().asEntityTypeOracles();
		
		Map<String, EntityType<?>> result = new HashMap<>();
		
		for (EntityTypeOracle subTypeOracle: subTypeOracles) {
			EntityType<?> subType = subTypeOracle.asType();
			for (Property property: subType.getProperties()) {
				result.merge(property.getName(), subType, (k,v) -> GenericEntity.T);
			}
		}
		
		// cleanup ambiguous entries detectable by null value
		for (Iterator<Map.Entry<String, EntityType<?>>> it = result.entrySet().iterator(); it.hasNext();) {
			Entry<String, EntityType<?>> entry = it.next();
			EntityType<?> value = entry.getValue();
			
			if (value == GenericEntity.T)
				it.remove();
		}
		
		return result;
	}
	
	@Override
	public GenericModelType getInferredPropertyType(EntityType<?> entityType, Property property) {
		if (propertyTypeInferenceOverride != null) {
			GenericModelType type = propertyTypeInferenceOverride.apply(entityType, property);
			if (type != null)
				return type;
		}
		
		return property.getType();
	}

}

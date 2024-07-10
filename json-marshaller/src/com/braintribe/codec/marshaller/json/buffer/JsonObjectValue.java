package com.braintribe.codec.marshaller.json.buffer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.braintribe.codec.marshaller.api.IdentityManagementMode;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.fasterxml.jackson.core.JsonLocation;

public class JsonObjectValue extends JsonComplexValue {
	private static final MapType STRING_OBJECT_MAP_TYPE = GMF.getTypeReflection().getMapType(EssentialTypes.TYPE_STRING, EssentialTypes.TYPE_OBJECT);
	private List<JsonField> fields = new ArrayList<>();
	private JsonField _refField;
	private JsonField _typeField;
	private JsonField _idField;
	private JsonField idField;
	private JsonField globalIdField;
	
	public JsonObjectValue(ConversionContext context, JsonLocation start) {
		super(context, start);
	}

	@Override
	public void addValue(JsonName name, JsonValue value) {
		JsonField field = buildField(name, value);
		
		fields.add(field);
	}

	private JsonField buildField(JsonName name, JsonValue value) {
		String n = name.getValue();
		
		SpecialField specialField = conversionContext.detectSpecialField(n);
		
		if (specialField == null)
			return new JsonField(name, value, true);
		
		JsonField field = new JsonField(name, value, specialField.isProperty);
		
		switch (specialField) {
			case _id: _idField = field; break;
			case _ref: _refField = field; break;
			case _type: _typeField = field; break;
			case id: idField = field; break;
			case globalId: globalIdField = field; break;
		}
		
		return field;
	}
	
	@Override
	public void onEnd() {
		// TODO Auto-generated method stub
		
	}

	private GenericModelType determineType(JsonValue value) throws ConversionError {
		String type = value.asString();
		
		GenericModelType gmType = GMF.getTypeReflection().findType(type);
		
		if (gmType != null)
			return gmType;
		
		// special type
		switch (type) {
			case "flatmap": return EssentialTypes.TYPE_MAP;
			case "map": return EssentialTypes.TYPE_MAP;
			case "set": return EssentialTypes.TYPE_SET;
			default: {
				String msg = "Unknown type [" + type + "] " + value.getErrorLocation();
				NotFound notFound = Reasons.build(NotFound.T).text(msg).toReason();
				throw new ConversionError(notFound);
			}
		}
	}
	
	@Override
	public Object as(GenericModelType inferredType) throws ConversionError {
		switch (inferredType.getTypeCode()) {
			case objectType: return asObject();
			case entityType: return asEntity((EntityType<?>)inferredType);
			case mapType: return asMap((MapType)inferredType);
			default:
				return typedAs(inferredType);
		}
	}
	
	private Object asObject() throws ConversionError {
		if (_typeField != null) {
			return typedAs(EssentialTypes.TYPE_OBJECT);
		}
		else if (conversionContext.identityManagedMode() == IdentityManagementMode._id && _refField != null) {
			return asRef(GenericEntity.T);
		}

		return asNativeMap(STRING_OBJECT_MAP_TYPE);
	}
	
	private Object typedAs(GenericModelType inferredType) throws ConversionError {
		GenericModelType explicitType = determineType(_typeField.value);

		Object value = null;
		
		if (explicitType.isEntity()) {
			value = buildEntity((EntityType<?>)explicitType);
		}
		else {
			JsonValue jsonValue = getSpecialValue();
			value = jsonValue.as(explicitType);
		}

		if (value != null && !inferredType.isInstance(value))
			throw conversionError(inferredType, explicitType);
		
		return value;
	}
	
	private Map<Object, Object> asMap(MapType mapType) throws ConversionError {
		if (_typeField != null) {
			return (Map<Object, Object>)typedAs(mapType);
		}
		
		return asNativeMap(mapType);
	}
	
	private Map<Object, Object> asNativeMap(MapType mapType) throws ConversionError {
		
		Map<Object, Object> map = new LinkedHashMap<>();
		
		GenericModelType keyType = mapType.getKeyType();
		GenericModelType valueType = mapType.getValueType();
		
		for (JsonField field: fields) {
			Object key = null;
			Object value = null;
			
			try {
				key = field.name.as(keyType); 
			}
			catch (ConversionError error) {
				String msg = "Invalid map key " + JsonLocations.toString(field.name.start);
				InvalidArgument reason = Reasons.build(InvalidArgument.T).text(msg).toReason();
				throw new ConversionError(reason, error);
			}
			
			try {
				value = field.value.as(valueType); 
			}
			catch (ConversionError error) {
				String msg = "Invalid map value " + JsonLocations.toString(field.value.start);
				InvalidArgument reason = Reasons.build(InvalidArgument.T).text(msg).toReason();
				throw new ConversionError(reason, error);
			}
			
			map.put(key, value);
		}
		
		return map;
	}
	
	private GenericEntity asRef(EntityType<?> inferredType) throws ConversionError {
		if (fields.size() != 1) {
			InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T) //
					.text("Invalid entity reference object literal due to invalid fields " + JsonLocations.toString(start)) //
					.toReason();
			
			throw new ConversionError(invalidArgument);
		}
		
		String ref = _refField.value.asString();
		
		GenericEntity entity = conversionContext.resolveReference(ref);
		
		if (entity == null) {
			NotFound notFound = Reasons.build(NotFound.T) //
					.text("No entity with ref id [" + ref + "] found " + JsonLocations.toString(_refField.value.start)) //
					.toReason();
				
			throw new ConversionError(notFound);
		}
		
		if (!inferredType.isAssignableFrom(entity.entityType()))
			throw conversionError(inferredType, entity.entityType());
		
		return entity;
	}

	private EntityType<?> concretizeEntityType(EntityType<?> inferredType) throws ConversionError {
		if (_typeField == null)
			return inferredType;
		
		GenericModelType explicitType = determineType(_typeField.value);
		
		if (inferredType.isAssignableFrom(explicitType))
			return (EntityType<?>) explicitType;
	
		throw conversionError(inferredType, explicitType);
	}
	
	private GenericEntity asEntityOrRecurrence(EntityType<?> inferredType) throws ConversionError {
		EntityType<?> concreteType = concretizeEntityType(inferredType);
		
		if (idField != null) {
			Object entityId = idField.value.as(EssentialTypes.TYPE_OBJECT);
			GenericEntity entity = conversionContext.resolveEntityById(concreteType, entityId);
			
			if (entity != null)
				return entity;
		}
		
		if (globalIdField != null) {
			String entityGlobalId = globalIdField.value.asString();
			
			GenericEntity entity = conversionContext.resolveEntityByGlobalId(entityGlobalId);
			
			if (entity != null)
				return entity;
		}
		
		return buildEntity(concreteType);
	}

	private GenericEntity asEntity(EntityType<?> inferredEntityType) throws ConversionError {
		if (_typeField != null) {
			return (GenericEntity)typedAs(inferredEntityType);
		}
		else {
			switch (conversionContext.identityManagedMode()) {
				case _id:
					if (_refField != null) 
						return asRef(inferredEntityType);
					break;
					
				case id:
					if (idField != null || globalIdField != null)
						return asEntityOrRecurrence(inferredEntityType);
					break;
					
				case auto: break;
				case off: break;
			}
		}
			
		return buildEntity(inferredEntityType);
	}
	
	private GenericEntity buildEntity(EntityType<?> entityType) throws ConversionError {
		
		if (entityType.isAbstract())
			entityType = resolvePolymorphicType(entityType);
		
		GenericEntity entity = conversionContext.createEntity(entityType);
		EntityBuilder entityBuilder = new EntityBuilder(entity, conversionContext);
		
		registerEntityIfRequired(entity);
		
		for (JsonField field: fields) {
			if (!field.property)
				continue;
			
			entityBuilder.setField(field);
		}
		
		return entity;
	}

	private EntityType<?> resolvePolymorphicType(EntityType<?> entityType) throws ConversionError {
		Map<String, EntityType<?>> specificProperties = conversionContext.getTypeSpecificProperties(entityType);
		
		for (JsonField field: fields) {
			if (!field.property)
				continue;
			
			String propertyName = field.name.getValue();
			
			EntityType<?> result = specificProperties.get(propertyName);
			
			if (result != null)
				return result;
		}
		
		NotFound notFound = Reasons.build(NotFound.T) //
				.text("Cannot resolve polymorphic ambiguity for abstract entity type [" + entityType.getTypeSignature() + "] " + getErrorLocation()) //
				.toReason();
		
		throw new ConversionError(notFound);
	}

	private void registerEntityIfRequired(GenericEntity entity) throws ConversionError {
		if (_idField != null) {
			String refId = _idField.value.asString();
			
			if (!conversionContext.registerEntity(entity, refId)) {
				InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text("Duplicate _id [" + idField + "] for entity").toReason();
				throw new ConversionError(invalidArgument);
			}
		}
		
		if (idField != null) {
			Object entityId = idField.value.as(EssentialTypes.TYPE_OBJECT);
			
			if (entityId != null)
				conversionContext.registerEntityById(entity, entityId);
		}
		
		if (globalIdField != null) {
			String entityGlobalId = globalIdField.value.asString();
			conversionContext.registerEntityByGlobalId(entity, entityGlobalId);
		}
	}

	private JsonValue getSpecialValue() throws ConversionError {
		if (_typeField == null) {
			NotFound notFound = Reasons.build(NotFound.T) //
					.text("Missing _type field " + JsonLocations.toString(_refField.value.start)) //
					.toReason();
				
			throw new ConversionError(notFound);

		}
		
		for (JsonField field: fields) {
			if (field == _typeField)
				continue;
			
			String name = field.name.getValue();
			if (name.equals("value")) {
				return field.value;
			}
			else {
				InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T) //
						.text("Invalid value object literal due to invalid fields " + JsonLocations.toString(start)) //
						.toReason();
				
				throw new ConversionError(invalidArgument);
			}
		}

		NotFound notFound = Reasons.build(NotFound.T) //
				.text("Missing value field " + JsonLocations.toString(_refField.value.start)) //
				.toReason();
			
		throw new ConversionError(notFound);
	}
}

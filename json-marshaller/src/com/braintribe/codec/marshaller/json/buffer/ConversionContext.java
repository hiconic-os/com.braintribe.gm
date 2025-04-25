package com.braintribe.codec.marshaller.json.buffer;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.braintribe.codec.marshaller.api.IdentityManagementMode;
import com.braintribe.codec.marshaller.json.DateCoding;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

public interface ConversionContext {
	DateCoding getDateCoding();
	GenericEntity resolveReference(String ref);
	GenericEntity createEntity(EntityType<?> entityType);
	boolean registerEntity(GenericEntity entity, String id);
	BiFunction<EntityType<?>, String, Property> getPropertySupplier();
	BiFunction<EntityType<?>, Property, GenericModelType> getPropertyTypeInferenceOverride();
	boolean isPropertyLenient();
	boolean snakeCaseProperties();
	Function<String, GenericModelType> idTypeSupplier();
	IdentityManagementMode identityManagedMode();
	Map<String, EntityType<?>> getTypeSpecificProperties(EntityType<?> entityType);
	GenericModelType getInferredPropertyType(EntityType<?> entityType, Property property);
	void registerEntityById(GenericEntity entity, Object id);
	void registerEntityByGlobalId(GenericEntity entity, String entityGlobalId);
	GenericEntity resolveEntityById(EntityType<?> concreteType, Object entityId);
	GenericEntity resolveEntityByGlobalId(String entityGlobalId);
	SpecialField detectSpecialField(String n);
	boolean supportPlaceholders();
}

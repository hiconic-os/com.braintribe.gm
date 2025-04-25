package com.braintribe.codec.marshaller.json.buffer;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang.WordUtils;

import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EssentialTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.VdHolder;

public class EntityBuilder {
	private GenericEntity entity;
	private ConversionContext conversionContext;
	
	private final BiFunction<EntityType<?>, String, Property> propertySupplier;
	private final Function<String, GenericModelType> idTypeSupplier;
	
	private EntityType<GenericEntity> entityType;
	private boolean propertyLenient;
	private boolean snakeCaseProperties;

	
	public EntityBuilder(GenericEntity entity, ConversionContext conversionContext) {
		this.entity = entity;
		this.entityType = entity.entityType();
		this.conversionContext = conversionContext;
		this.propertySupplier = conversionContext.getPropertySupplier();
		this.propertyLenient = conversionContext.isPropertyLenient();
		this.snakeCaseProperties = conversionContext.snakeCaseProperties();
		this.idTypeSupplier = conversionContext.idTypeSupplier();
	}

	protected String toCamelCase(String value, char delimiter) {
		String pascalCase = WordUtils.capitalizeFully(value, new char[] { delimiter }).replace(Character.toString(delimiter), "");
		return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
	}
	
	public Property resolveProperty(String name) {
		if (snakeCaseProperties) {
			name = toCamelCase(name, '_');
		}
		
		return propertySupplier != null ? propertySupplier.apply(entityType, name) : entityType.findProperty(name);
	}

	private MappingError propertyNotFound(String propertyName, JsonField field) {
		String msg = "Unknown property [" + propertyName + "] within type " + entityType.getTypeSignature() + " " + field.name.getSpan();
		NotFound notFound = Reasons.build(NotFound.T).text(msg).toReason(); 
		return new MappingError(notFound);
	}
	
	private MappingError propertyValueMismatch(String propertyName, ConversionError e, JsonField field) {
		String msg = "Invalid value for property [" + propertyName + "] within type " + entityType.getTypeSignature() + " " + field.name.getSpan();
		InvalidArgument invalidArgument = Reasons.build(InvalidArgument.T).text(msg).toReason();
		invalidArgument.getReasons().add(e.getReason());
		return new MappingError(invalidArgument);
	}
	
	public void setField(JsonField field) {
		String name = field.name.getValue();

		Property property = null;
		
		if (name.charAt(0) == '?') {

			String realName = name.substring(1);
			property = resolveProperty(realName);
			if (property == null) {
				if (propertyLenient)
					return;
				
				throw propertyNotFound(realName, field);
			}

			try {
				AbsenceInformation ai = (AbsenceInformation) field.value.as(AbsenceInformation.T);
				property.setAbsenceInformation(entity, ai);
			}
			catch (ConversionError e) {
				throw propertyValueMismatch(realName, e, field);
			}

		} else {
			property = resolveProperty(name);
			if (property == null) {
				if (propertyLenient)
					return;
				
				throw propertyNotFound(name, field);
			}
			
			GenericModelType propertyType = conversionContext.getInferredPropertyType(entityType, property);
			
			JsonValue jsonValue = field.value;
			
			try {
				if (property.isIdentifier()) {
					if (idTypeSupplier != null) { 
						propertyType = idTypeSupplier.apply(entityType.getTypeSignature());
					}
					else {
						Object id = jsonValue.as(EssentialTypes.TYPE_OBJECT);
						
						if (id instanceof Integer)
							id = Long.valueOf((Integer)id);
						
						property.set(entity, id);
						return;
					}
				}
			
				Object castedValue = jsonValue.as(propertyType);
				
				if (VdHolder.isVdHolder(castedValue)) {
					property.setDirectUnsafe(entity, castedValue);
				}
				else {
					property.set(entity, castedValue);
				}
			}
			catch (ConversionError e) {
				throw propertyValueMismatch(name, e, field);
			}
		}

	}

}

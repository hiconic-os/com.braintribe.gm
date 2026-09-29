// ============================================================================
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.model.processing.aspect.crypto.interceptor.manipulation;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.crypto.Encryptor;
import com.braintribe.logging.Logger;
import com.braintribe.model.accessapi.ManipulationRequest;
import com.braintribe.model.accessapi.ManipulationResponse;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.EntityProperty;
import com.braintribe.model.generic.manipulation.LocalEntityProperty;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.Owner;
import com.braintribe.model.generic.manipulation.PropertyManipulation;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.StandardCloningContext;
import com.braintribe.model.generic.reflection.StrategyOnCriterionMatch;
import com.braintribe.model.processing.aop.api.context.AroundContext;
import com.braintribe.model.processing.aspect.crypto.interceptor.AbstractCryptoInterceptorProcessor;
import com.braintribe.model.processing.aspect.crypto.interceptor.CryptoInterceptionException;
import com.braintribe.model.processing.aspect.crypto.interceptor.CryptoInterceptorConfiguration;

/** Encrypts changed values for properties carrying {@code PropertyCrypting} metadata. */
public class CryptoManipulationInterceptorProcessor extends AbstractCryptoInterceptorProcessor<ManipulationRequest, ManipulationResponse> {
	private static final Logger log = Logger.getLogger(CryptoManipulationInterceptorProcessor.class);

	private final Map<PropertyManipulation, Object> encryptedValues = new HashMap<>();

	protected CryptoManipulationInterceptorProcessor(CryptoInterceptorConfiguration configuration,
			AroundContext<ManipulationRequest, ManipulationResponse> aroundContext) {
		super(configuration, aroundContext, log);
	}

	@Override
	protected boolean mustProcessRequest() throws CryptoInterceptionException {
		encryptChangedValues(aroundContext.getRequest().getManipulation());
		return !encryptedValues.isEmpty();
	}

	@Override
	protected ManipulationRequest processRequest() throws CryptoInterceptionException {
		try {
			return (ManipulationRequest) aroundContext.getRequest().entityType().clone(new NewValueReplacingCloningContext(),
					aroundContext.getRequest(), StrategyOnCriterionMatch.reference);
		} catch (Exception e) {
			throw cryptoError("Failed to clone the manipulation request", e);
		}
	}

	private void encryptChangedValues(Manipulation manipulation) throws CryptoInterceptionException {
		if (manipulation instanceof CompoundManipulation) {
			CompoundManipulation compound = (CompoundManipulation) manipulation;
			for (Manipulation nested : compound.getCompoundManipulationList())
				encryptChangedValues(nested);
		} else if (manipulation instanceof PropertyManipulation) {
			encryptChangedValues((PropertyManipulation) manipulation);
		}
	}

	private void encryptChangedValues(PropertyManipulation manipulation) throws CryptoInterceptionException {
		if (!(manipulation instanceof ChangeValueManipulation) && !(manipulation instanceof AddManipulation))
			return;

		Owner owner = manipulation.getOwner();
		if (owner == null || owner.getPropertyName() == null)
			throw new CryptoInterceptionException("Cannot encrypt a manipulation without a property owner");

		Encryptor encryptor = mustEncrypt(entityType(owner), owner.getPropertyName());
		if (encryptor == null)
			return;

		if (manipulation instanceof ChangeValueManipulation)
			encryptChange(encryptor, (ChangeValueManipulation) manipulation);
		else
			encryptAdd(encryptor, (AddManipulation) manipulation);
	}

	private void encryptChange(Encryptor encryptor, ChangeValueManipulation manipulation) throws CryptoInterceptionException {
		if (!(manipulation.getNewValue() instanceof String))
			return;
		String value = (String) manipulation.getNewValue();
		try {
			encryptedValues.put(manipulation, encryptor.encrypt(value).result().asString());
		} catch (Exception e) {
			throw cryptoError("Failed to encrypt value", e);
		}
	}

	private void encryptAdd(Encryptor encryptor, AddManipulation manipulation) throws CryptoInterceptionException {
		if (manipulation.getItemsToAdd() == null)
			return;

		Map<Object, Object> encryptedItems = new HashMap<>();
		boolean changed = false;
		try {
			for (Map.Entry<Object, Object> entry : manipulation.getItemsToAdd().entrySet()) {
				Object value = entry.getValue();
				if (value instanceof String) {
					value = encryptor.encrypt((String) value).result().asString();
					changed = true;
				}
				encryptedItems.put(entry.getKey(), value);
			}
		} catch (Exception e) {
			throw cryptoError("Failed to encrypt value", e);
		}
		if (changed)
			encryptedValues.put(manipulation, encryptedItems);
	}

	private EntityType<? extends GenericEntity> entityType(Owner owner) throws CryptoInterceptionException {
		if (owner instanceof EntityProperty) {
			EntityProperty entityProperty = (EntityProperty) owner;
			EntityType<? extends GenericEntity> entityType = GMF.getTypeReflection()
					.getEntityType(entityProperty.getReference().getTypeSignature());
			if (entityType == null)
				throw new CryptoInterceptionException("Unknown entity type: " + entityProperty.getReference().getTypeSignature());
			return entityType;
		}
		if (owner instanceof LocalEntityProperty)
			return ((LocalEntityProperty) owner).getEntity().entityType();
		throw new CryptoInterceptionException("Unsupported property owner: " + owner);
	}

	private class NewValueReplacingCloningContext extends StandardCloningContext {
		private final Map<GenericEntity, String> replacedProperties = new HashMap<>();

		@Override
		@SuppressWarnings("unchecked")
		public GenericEntity supplyRawClone(EntityType<? extends GenericEntity> entityType, GenericEntity source) {
			GenericEntity clone = super.supplyRawClone(entityType, source);
			Object encryptedValue = encryptedValues.get(source);
			if (encryptedValue == null)
				return clone;

			if (clone instanceof ChangeValueManipulation) {
				((ChangeValueManipulation) clone).setNewValue(encryptedValue);
				replacedProperties.put(clone, "newValue");
			} else if (clone instanceof AddManipulation) {
				((AddManipulation) clone).setItemsToAdd((Map<Object, Object>) encryptedValue);
				replacedProperties.put(clone, "itemsToAdd");
			}
			return clone;
		}

		@Override
		public boolean canTransferPropertyValue(EntityType<? extends GenericEntity> entityType, Property property, GenericEntity source,
				GenericEntity clone, AbsenceInformation sourceAbsenceInformation) {
			String replacedProperty = replacedProperties.get(clone);
			return !property.getName().equals(replacedProperty)
					&& super.canTransferPropertyValue(entityType, property, source, clone, sourceAbsenceInformation);
		}
	}
}

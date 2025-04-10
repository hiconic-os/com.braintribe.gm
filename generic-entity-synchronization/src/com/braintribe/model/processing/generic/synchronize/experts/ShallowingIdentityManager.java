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
package com.braintribe.model.processing.generic.synchronize.experts;

import com.braintribe.cfg.Configurable;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.generic.synchronize.EntityNotFoundInSessionException;
import com.braintribe.model.processing.generic.synchronize.GenericEntitySynchronizationException;
import com.braintribe.model.processing.generic.synchronize.api.IdentityManager;
import com.braintribe.model.processing.generic.synchronize.api.SynchronizationContext;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;

/**
 * An abstract {@link IdentityManager} implementation that can be used for managing shallow instances. <br />
 * Typically that's used for components of {@link GmMetaModel} that should be synchronized separately. <br />
 * That means that an entity is either found in the target session or a shallow instance will be created for reference. 
 * Synchronizing other properties is disabled. 
 */
public abstract class ShallowingIdentityManager<T extends GenericEntity> extends QueryingIdentityManager {

	private static Logger logger = Logger.getLogger(ShallowingIdentityManager.class);
	
	protected static GenericModelTypeReflection typeReflection = GMF.getTypeReflection();
	private EntityType<T> shallowType;
	private boolean entityRequiredInSession = false;
	
	public ShallowingIdentityManager(EntityType<T> shallowType) {
		this.shallowType = shallowType;
	}

	@Configurable
	public void setEntityRequiredInSession(boolean entityRequiredInSession) {
		this.entityRequiredInSession = entityRequiredInSession;
	}
	
	/**
	 * @see com.braintribe.model.processing.generic.synchronize.api.IdentityManager#isResponsible(com.braintribe.model.processing.session.api.persistence.PersistenceGmSession, com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.reflection.EntityType)
	 */
	@Override
	public boolean isResponsible(GenericEntity instance, EntityType<? extends GenericEntity> entityType, SynchronizationContext context) {
		if (shallowType.isAssignableFrom(entityType)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @see com.braintribe.model.processing.generic.synchronize.api.IdentityManager#findEntity(com.braintribe.model.processing.session.api.persistence.PersistenceGmSession, com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.reflection.EntityType, java.util.Set)
	 */
	@Override
	public GenericEntity findEntity(GenericEntity instanceToBeCloned,EntityType<? extends GenericEntity> entityType, SynchronizationContext context) throws GenericEntitySynchronizationException {
		PersistenceGmSession session = context.getSession();
		GenericEntity existing = null;
		if (shallowType.isAssignableFrom(entityType)) {
			@SuppressWarnings("unchecked")
			T castedInstance = (T) instanceToBeCloned;
			EntityQuery query = getQuery(session, castedInstance, entityType);
			if (query != null) {
				existing = query(session, query);
				if (existing == null) {
					if (entityRequiredInSession) {
						throw new EntityNotFoundInSessionException("Following entity was expected to be found in session but wasn't. Type: "+entityType.getTypeSignature()+". Instance: "+instanceToBeCloned);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Entity of type: "+entityType.getTypeSignature()+" not found in session. Create shallow instance.");
					}
					existing = createShallowInstance(session, castedInstance, entityType);
				}
			}
		} else {
			logger.warn("The passed entityType: "+entityType.getTypeSignature()+" is not supported. Expected: "+shallowType.getTypeSignature());
		}
		return existing;
	}

	/**
	 * @see com.braintribe.model.processing.generic.synchronize.api.IdentityManager#canTransferProperty(com.braintribe.model.processing.session.api.persistence.PersistenceGmSession, com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.GenericEntity, com.braintribe.model.generic.reflection.EntityType, com.braintribe.model.generic.reflection.Property, java.util.Set)
	 */
	@Override
	public boolean canTransferProperty(GenericEntity instanceToBeCloned, GenericEntity clonedInstance, EntityType<? extends GenericEntity> entityType, Property property, SynchronizationContext context) {
		return false;
	}

	/**
	 * Called if no instance can be found in target session to provide the shallow instance.
	 */
	protected abstract GenericEntity createShallowInstance(PersistenceGmSession session, T instanceToBeCloned, EntityType<?> entityType);

	/**
	 * Provides the lookup query used for searching existing instances in target session. 
	 */
	protected abstract EntityQuery getQuery(PersistenceGmSession session, T instanceToBeCloned, EntityType<?> entityType);
	
	
}

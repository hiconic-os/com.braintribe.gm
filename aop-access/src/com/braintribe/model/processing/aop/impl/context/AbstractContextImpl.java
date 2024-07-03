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
package com.braintribe.model.processing.aop.impl.context;

import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.generic.session.exception.GmSessionException;
import com.braintribe.model.processing.aop.api.aspect.AccessJoinPoint;
import com.braintribe.model.processing.aop.api.context.Context;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

/**
 * @author dirk, pit
 *
 */
public abstract class AbstractContextImpl<I> implements Context<I>{

	
	private static Logger log = Logger.getLogger(AbstractContextImpl.class);
	
	protected I request;

	private AccessJoinPoint joinPoint;	
	private static GenericModelTypeReflection typeReflection = GMF.getTypeReflection();	
	private PersistenceGmSession userSession;
	private PersistenceGmSession systemSession;
	

	
	// **************************************************************************
	// Constructor
	// **************************************************************************

	/**
	 * Default constructor
	 */
	public AbstractContextImpl() {
	}

	// **************************************************************************
	// Getter/Setter
	// **************************************************************************
			
	
	@Override
	public I getRequest() {
		return request;
	}	
	public void setRequest( I request) {
		this.request = request;
	}

	@Override
	public AccessJoinPoint getJointPoint() {
		return joinPoint;
	}
	public void setJoinPoint(AccessJoinPoint joinPoint) {
		this.joinPoint = joinPoint;
	}
	
	public void setSession(PersistenceGmSession userSession) {
		this.userSession = userSession;
	}

	@Override
	public PersistenceGmSession getSession() {		
		return userSession;
	}
	
	public void setSystemSession(PersistenceGmSession systemSession) {
		this.systemSession = systemSession;
	}

	@Override
	public PersistenceGmSession getSystemSession() {		
		return systemSession;
	}
	
	protected void commitIfNecessary( PersistenceGmSession session) throws GmSessionException {
		if (session.getTransaction().hasManipulations()) {
			session.commit();
		}
	}
	
	public void commitIfNecessary() throws GmSessionException{
		try {
			commitIfNecessary(systemSession);
		} catch (GmSessionException e) {
			String msg = "cannot commit system session for joinPoint [" + joinPoint + "], advice [" + getAdvice() +"]";
			log.error( msg, e);
			throw e;
		}
		try {
			commitIfNecessary(userSession);
		} catch (GmSessionException e) {
			String msg = "cannot commit user session for joinPoint [" + joinPoint + "], advice [" + getAdvice() +"]";
			log.error( msg, e);
			throw e;
		}
	}

}

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
package com.braintribe.codec.dom.genericmodel;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.braintribe.codec.context.CodingContext;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.session.GmSession;

public class DecodingContext extends CodingContext {
	private Map<String, Object> decodedEntities = new HashMap<String, Object>();
	private int version = 1;
	private Map<String, Element> pool; 
	private com.braintribe.codec.marshaller.api.DecodingLenience lenience = new com.braintribe.codec.marshaller.api.DecodingLenience();
	private boolean assignAbsenceInformationForMissingProperties = false;
	private AbsenceInformation absenceInformationForMissingProperties = null;
	private GmSession session;
	
	public DecodingContext(GmDeserializationOptions options) {
		this.session = options.getSession();
	}
	
	public void setAssignAbsenceInformationForMissingProperties(boolean assignAbsenceInformationForMissingProperties) {
		this.assignAbsenceInformationForMissingProperties = assignAbsenceInformationForMissingProperties;
	}
	
	public boolean shouldAssignAbsenceInformationForMissingProperties() {
		return assignAbsenceInformationForMissingProperties;
	}
	
	public AbsenceInformation getAbsenceInformationForMissingProperties() {
		if (absenceInformationForMissingProperties == null) {			
			absenceInformationForMissingProperties = GMF.absenceInformation();
		}

		return absenceInformationForMissingProperties;
	}
	
	public void setPool(Map<String, Element> pool) {
		this.pool = pool;
	}
	
	public Element getPooledElement(String ref){
		Element element = pool != null? pool.get(ref): null;
		
		return element;
	}
	
	public Map<String, Element> getPool() {
		return pool;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getVersion() {
		return version;
	}

	
	public void register(String id, Object entity) {
		decodedEntities.put(id, entity);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T getEntity(String id) {
		return (T)decodedEntities.get(id);
	}

	public com.braintribe.codec.marshaller.api.DecodingLenience getLenience() {
		return lenience;
	}

	public void setLenience(com.braintribe.codec.marshaller.api.DecodingLenience lenience) {
		this.lenience = lenience;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends GenericEntity> T createRaw(EntityType<?> entityType) {
		return (T)(session != null? session.createRaw(entityType): entityType.createRaw());
	}
}

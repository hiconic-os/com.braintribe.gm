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
package com.braintribe.model.processing.query.selection.experts;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.data.prompt.Name;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.Source;
import com.braintribe.utils.i18n.I18nTools;

/**
 * Based on the {@link SimpleAliasResolver}, but uses the {@link Name} metadata, if present.
 * @author michel.docouto
 *
 */
public class NameAliasResolver extends SimpleAliasResolver {
	
	private PersistenceGmSession gmSession;
	
	public void setGmSession(PersistenceGmSession gmSession) {
		this.gmSession = gmSession;
	}
	
	@Override
	public String getAliasForSource(Source source) {
		EntityType<GenericEntity> entityType = getEntityType(source);
		if (entityType != null) {
			if (gmSession != null) {
				Name name = gmSession.getModelAccessory().getMetaData().lenient(true).entityType(entityType).meta(Name.T).exclusive();
				if (name != null && name.getName() != null)
					return I18nTools.getLocalized(name.getName());
			}
			
			return resolveTypeSignature(entityType.getTypeSignature());
		}
		
		return null;
	}
	
	@Override
	public String getPropertyNameForSource(Source source, String propertyName) {
		if (gmSession == null)
			return propertyName;
		
		EntityType<GenericEntity> entityType = getEntityType(source);
		if (entityType == null)
			return propertyName;
		
		Name name = gmSession.getModelAccessory().getMetaData().lenient(true).entityType(entityType).property(propertyName).meta(Name.T).exclusive();
		if (name == null || name.getName() == null)
			return propertyName;
		
		return I18nTools.getLocalized(name.getName());
	}

}

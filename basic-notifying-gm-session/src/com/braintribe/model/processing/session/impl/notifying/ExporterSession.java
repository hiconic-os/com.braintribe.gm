// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.session.impl.notifying;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.VdePropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.processing.session.api.notifying.interceptors.VdEvaluation;

public class ExporterSession extends BasicNotifyingGmSession {
	public ExporterSession() {
		interceptors().with(VdEvaluation.class).add(new VdePropertyAccessInterceptor());
		
		interceptors().add(new PropertyAccessInterceptor() {
			
			@Override
			public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {
				return next.setProperty(property, entity, value, isVd);
			}
			
			@Override
			public Object getProperty(Property property, GenericEntity entity, boolean isVd) {
				return next.getProperty(property, entity, isVd);
			}
			
		});
	}
	
	@Override
	public <T extends GenericEntity> T create(EntityType<T> entityType) {
		T entity = super.create(entityType);
		for (Property property: entityType.getProperties()) {
			property.setAbsenceInformation(entity, GMF.absenceInformation());
		}
		
		return entity;
	}
}

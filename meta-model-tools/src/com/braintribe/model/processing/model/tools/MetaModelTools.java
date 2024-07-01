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
package com.braintribe.model.processing.model.tools;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.GenericModelTypeReflection;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.util.meta.NewMetaModelGeneration;

/**
 * 
 */
public class MetaModelTools {

	private static final GenericModelTypeReflection typeReflection = GMF.getTypeReflection();

	@SafeVarargs
	public static GmMetaModel provideRawModel(EntityType<?>... types) {
		return provideRawModel("gm:Model", asList(types));
	}

	public static GmMetaModel provideRawModel(Collection<EntityType<?>> types) {
		return provideRawModel("gm:Model", types);
	}

	public static GmMetaModel provideRawModel(String name, Collection<EntityType<?>> types) {
		return new NewMetaModelGeneration().buildMetaModel(name, types);
	}

	public static Set<EntityType<? extends GenericEntity>> getEntityTypesFor(Set<Class<?>> classes) throws GenericModelException {
		Set<EntityType<? extends GenericEntity>> entityTypes = new HashSet<EntityType<? extends GenericEntity>>();

		for (Class<?> entityClass: classes) {
			entityTypes.add(typeReflection.getEntityType((Class<? extends GenericEntity>) entityClass));
		}

		return entityTypes;
	}

	public static GmMetaModel modelFor(Set<EntityType<?>> types) {
		return provideRawModel(types);
	}
}

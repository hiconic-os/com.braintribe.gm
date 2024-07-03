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
package com.braintribe.model.generic.reflection;

import java.util.HashMap;
import java.util.Map;

import com.braintribe.model.generic.GmPlatform;
import com.braintribe.model.generic.reflection.type.BaseTypeImpl;
import com.braintribe.model.generic.reflection.type.collection.ListTypeImpl;
import com.braintribe.model.generic.reflection.type.collection.MapTypeImpl;
import com.braintribe.model.generic.reflection.type.collection.SetTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.BooleanTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.DateTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.DecimalTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.DoubleTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.FloatTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.IntegerTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.LongTypeImpl;
import com.braintribe.model.generic.reflection.type.simple.StringTypeImpl;

/**
 * @author peter.gazdik
 */
public abstract class AbstractGmPlatform implements GmPlatform {

	private static final Map<Class<?>, GenericModelType> essentialTypeMap = new HashMap<>();

	static {
		register(BaseTypeImpl.INSTANCE);

		register(BooleanTypeImpl.INSTANCE);
		register(IntegerTypeImpl.INSTANCE);
		register(LongTypeImpl.INSTANCE);
		register(FloatTypeImpl.INSTANCE);
		register(DoubleTypeImpl.INSTANCE);
		register(DecimalTypeImpl.INSTANCE);
		register(DateTypeImpl.INSTANCE);
		register(StringTypeImpl.INSTANCE);

		register(new ListTypeImpl(BaseTypeImpl.INSTANCE));
		register(new SetTypeImpl(BaseTypeImpl.INSTANCE));
		register(new MapTypeImpl(BaseTypeImpl.INSTANCE, BaseTypeImpl.INSTANCE));

	}

	private static void register(GenericModelType type) {
		essentialTypeMap.put(type.getJavaType(), type);
	}

	@Override
	public <T extends GenericModelType> T getEssentialType(Class<?> javaType) {
		return (T) essentialTypeMap.get(javaType);
	}


}

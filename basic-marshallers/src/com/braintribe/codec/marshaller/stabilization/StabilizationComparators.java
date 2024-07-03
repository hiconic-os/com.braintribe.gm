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
package com.braintribe.codec.marshaller.stabilization;

import java.util.Comparator;

import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.generic.reflection.GenericModelType;

public abstract class StabilizationComparators {
	public static Comparator<?> comparator(GenericModelType type) {
		switch (type.getTypeCode()) {
		case objectType:
			return ObjectComparator.INSTANCE;
			
		case entityType:
			return EntityComparator.INSTANCE;

		case enumType:
		case booleanType:
		case dateType:
		case decimalType:
		case doubleType:
		case floatType:
		case integerType:
		case longType:
		case stringType:
			return ComparableComparator.INSTANCE;
		default:
			throw new GenericModelException("unsupported value type for comparision " + type);
		}
	}
}

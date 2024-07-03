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
package com.braintribe.model.processing.query.stringifier;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.processing.core.expert.api.GmExpertRegistry;
import com.braintribe.model.processing.query.api.stringifier.experts.Stringifier;
import com.braintribe.model.processing.query.api.stringifier.experts.StringifierContext;
import com.braintribe.model.processing.query.stringifier.experts.EnumStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.BooleanStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.CollectionStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.DateStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.DefaultStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.NullStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.NumberStringifier;
import com.braintribe.model.processing.query.stringifier.experts.basic.StringStringifier;

public class BasicStringifierContext implements StringifierContext {
	private GmExpertRegistry expertRegistry;

	protected Stringifier<Object, StringifierContext> defaultStringifier = new DefaultStringifier();
	protected Stringifier<Void, StringifierContext> nullStringifier = new NullStringifier();

	// Simple Types
	protected Stringifier<String, StringifierContext> stringStringifier = new StringStringifier();
	protected Stringifier<Boolean, StringifierContext> booleanStringifier = new BooleanStringifier();
	protected Stringifier<Integer, StringifierContext> integerStringifier = new NumberStringifier<Integer>();
	protected Stringifier<Long, StringifierContext> longStringifier = new NumberStringifier<Long>("l");
	protected Stringifier<Double, StringifierContext> doubleStringifier = new NumberStringifier<Double>("d");
	protected Stringifier<BigDecimal, StringifierContext> decimalStringifier = new NumberStringifier<BigDecimal>("b");
	protected Stringifier<Float, StringifierContext> floatStringifier = new NumberStringifier<Float>("f");
	protected Stringifier<Date, StringifierContext> dateStringifier = new DateStringifier();

	protected Stringifier<Enum<?>, BasicQueryStringifierContext> enumStringifier = new EnumStringifier();
	protected Stringifier<Collection<?>, StringifierContext> collectionStringifier = new CollectionStringifier();

	public void setExpertRegistry(GmExpertRegistry expertRegistry) {
		this.expertRegistry = expertRegistry;
	}

	@Override
	public String stringify(Object object) {
		if (object == null) {
			return this.nullStringifier.stringify(null, this);
		}

		GenericModelType type = GMF.getTypeReflection().getType(object);
		switch (type.getTypeCode()) {
		case stringType:
			return this.stringStringifier.stringify((String) object, this);
		case booleanType:
			return this.booleanStringifier.stringify((Boolean) object, this);
		case dateType:
			return this.dateStringifier.stringify((Date) object, this);
		case integerType:
			return this.integerStringifier.stringify((Integer) object, this);
		case longType:
			return this.longStringifier.stringify((Long) object, this);
		case decimalType:
			return this.decimalStringifier.stringify((BigDecimal) object, this);
		case floatType:
			return this.floatStringifier.stringify((Float) object, this);
		case doubleType:
			return this.doubleStringifier.stringify((Double) object, this);
		case entityType:
			return getEntityStringifier((GenericEntity) object).stringify(object, this);
		case enumType:
			return this.enumStringifier.stringify((Enum<?>) object, (BasicQueryStringifierContext) this);
		case setType:
		case listType:
			return this.collectionStringifier.stringify((Collection<?>) object, this);
		case mapType:
			/* maps are not supported as values by the QueryModel */
		default:
			throw new UnsupportedOperationException("Unsupported GenericModelType: " + type + " found for object: " + object);
		}
	}

	@Override
	public void stringifyAndAppend(Object object, StringBuilder builder) {
		builder.append(stringify(object));
	}

	private Stringifier<Object, StringifierContext> getEntityStringifier(GenericEntity entity) {
		GenericModelType type = entity.type();
		Stringifier<Object, StringifierContext> stringifier = this.expertRegistry.findExpert(Stringifier.class).forType(type);

		if (stringifier == null) {
			stringifier = this.defaultStringifier;
		}

		return stringifier;
	}
}

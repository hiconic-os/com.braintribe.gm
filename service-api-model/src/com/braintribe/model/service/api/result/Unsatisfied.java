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
package com.braintribe.model.service.api.result;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Unsatisfied extends ServiceResult {

	EntityType<Unsatisfied> T = EntityTypes.T(Unsatisfied.class);

	String whyUnsatisfied = "whyUnsatisfied";
	String value = "value";
	String hasValue = "hasValue";
	
	Reason getWhy();
	void setWhy(Reason whyUnsatisfied);
	
	Object getValue();
	void setValue(Object value);
	
	boolean getHasValue();
	void setHasValue(boolean hasValue);

	@Override
	default ServiceResultType resultType() {
		return ServiceResultType.unsatisfied;
	}

	@Override
	default Unsatisfied asUnsatisfied() {
		return this;
	}
	
	static Unsatisfied from(Maybe<?> maybe) {
		if (maybe.isSatisfied())
			throw new IllegalArgumentException("maybe must not be satisfied");
		
		Unsatisfied unsatisfied = Unsatisfied.T.create();
		unsatisfied.setWhy(maybe.whyUnsatisfied());

		if (maybe.isIncomplete()) {
			unsatisfied.setValue(maybe.value());
			unsatisfied.setHasValue(true);
		}

		return unsatisfied;
	}

	@Deprecated
	/**
	 * @deprecated use {@link #toMaybe()} instead
	 */
	default <T> Maybe<T> toMaby() {
		return toMaybe();
	}
	
	default <T> Maybe<T> toMaybe() {
		return getHasValue()? // 
				Maybe.incomplete((T)getValue(), getWhy()): // 
					Maybe.empty(getWhy());
	}
}

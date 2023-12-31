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
package com.braintribe.model.processing.service.api;

import com.braintribe.common.attribute.AttributeContextBuilder;
import com.braintribe.common.attribute.TypeSafeAttribute;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.ServiceRequest;

public interface ServiceRequestContextBuilder extends AttributeContextBuilder {
	
	@Override
	default <A extends TypeSafeAttribute<? super V>, V> ServiceRequestContextBuilder set(Class<A> attribute, V value) {
		setAttribute(attribute, value);
		return this;
	}
	ServiceRequestContextBuilder setEvaluator(Evaluator<ServiceRequest> evaluator);
	
	@Override
	ServiceRequestContext build();
}

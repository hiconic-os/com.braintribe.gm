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
package com.braintribe.model.processing.rpc.commons.impl.client;

import java.util.Deque;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.exception.Exceptions;
import com.braintribe.logging.ndc.mbean.NestedDiagnosticContext;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.service.api.aspect.RequestedEndpointAspect;
import com.braintribe.model.processing.service.api.aspect.RequestorAddressAspect;
import com.braintribe.model.processing.service.api.aspect.RequestorUserNameAspect;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Failure;
import com.braintribe.utils.StringTools;

public class GmRpcExceptionContextualizer {

	public static void enhanceMessage(Failure message, AttributeContext attributeContext, ServiceRequest request) {

		StringBuilder sb = new StringBuilder(message.getMessage() != null ? message.getMessage() : "(no message)");
		sb.append(" [");
		if (attributeContext != null) {
			sb.append("Context:");
			sb.append(stringify(attributeContext));
		}
		if (request != null) {
			if (sb.length() > 0) sb.append(", ");
			sb.append("Request:");
			sb.append(request.stringify());
		}
		String threadName = Thread.currentThread().getName();
		if (sb.length() > 0) sb.append(", ");
		sb.append("Threadname:");
		sb.append(threadName);
		sb.append(']');
		String enhancedMessage = sb.toString();
		
		message.setMessage(enhancedMessage);
	}
	
	public static <E extends Throwable> E enhanceException(E t, AttributeContext attributeContext, GenericEntity request) {
		String message = t.getMessage();
		StringBuilder sb = new StringBuilder(message != null ? message : "(no message)");
		sb.append(" [");
		if (attributeContext != null) {
			sb.append("Context:");
			sb.append(stringify(attributeContext));
		}
		if (request != null) {
			if (sb.length() > 0) sb.append(", ");
			sb.append("Request:");
			sb.append(request.stringify());
		}
		String threadName = Thread.currentThread().getName();
		if (sb.length() > 0) sb.append(", ");
		sb.append("Threadname:");
		sb.append(threadName);
		
		Deque<String> ndcCollection = NestedDiagnosticContext.getNdc();
		if (ndcCollection != null && !ndcCollection.isEmpty()) {
			String ndc = StringTools.createStringFromCollection(ndcCollection, ",");
			sb.append(", ndc: ");
			sb.append(ndc);
		}
		sb.append(']');
		String enhancedMessage = sb.toString();
		
		return Exceptions.contextualize(t, enhancedMessage);
	}
	
	public static String stringify(AttributeContext attributeContext) {
		StringBuilder sb = new StringBuilder(attributeContext.getClass().getSimpleName());
		sb.append('[');
		attributeContext.findAttribute(RequestedEndpointAspect.class).ifPresent(requestedEndpoint -> {
			sb.append("requested endpoint: ");
			sb.append(requestedEndpoint);
		});

		attributeContext.findAttribute(RequestorAddressAspect.class).ifPresent( requestorAddress -> {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("requestor address: ");
			sb.append(requestorAddress);
		});
		
		attributeContext.findAttribute(RequestorUserNameAspect.class).ifPresent( username -> {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("requestor user: ");
			sb.append(username);
		});
		
		sb.append(']');
		return sb.toString();
	}

}

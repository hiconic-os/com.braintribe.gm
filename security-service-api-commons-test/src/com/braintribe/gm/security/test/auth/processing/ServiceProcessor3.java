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
package com.braintribe.gm.security.test.auth.processing;

import java.io.UncheckedIOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarException;

import com.braintribe.gm.security.test.auth.model.ServiceRequest3;
import com.braintribe.model.processing.service.api.ServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

public class ServiceProcessor3<P extends ServiceRequest3, R extends Number> extends AuthorizedServiceProcessorBase implements ServiceProcessor<P, R> {

	public static final Integer RETURN = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
	public static final Class<? extends Exception> EXCEPTION_TYPE = UncheckedIOException.class;
	
	@SuppressWarnings("unchecked")
	@Override
	public R process(ServiceRequestContext context, P parameter) throws UncheckedIOException {
		if (parameter.getForceException()) {
			throw new UncheckedIOException(new JarException("Enforced business exception for: " + parameter));
		}
		validate(context, parameter);
		return (R) RETURN;
	}

}
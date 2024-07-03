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
package com.braintribe.model.processing.securityservice.api;

import java.util.concurrent.Callable;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.processing.securityservice.api.exceptions.SecurityServiceException;

public interface UserSessionScopingBuilder {
	
	UserSessionScope push() throws SecurityServiceException;
	
	void runInScope(Runnable runnable) throws SecurityServiceException;
	
	default Runnable scoped(Runnable runnable) {
		return () -> runInScope(runnable);
	}
	
	default <T> Callable<T> scoped(Callable<T> callable) {
		return () -> {
			
			class PromiseRunnable implements Runnable {
				Throwable throwable;
				T result;
				
				@Override
				public void run() {
					try {
						result = callable.call();
					} catch(Throwable e) {
						throwable = e;
					}
				}
			}
			PromiseRunnable runnable = new PromiseRunnable();
			
			runInScope(runnable);
			
			if (runnable.throwable != null) {
				Exception exception = Exceptions.normalizer(runnable.throwable).asExceptionOrThrowUnchecked();
				throw exception;
			} else {
				return runnable.result;
			}

		};
	}
}

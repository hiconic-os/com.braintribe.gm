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
package com.braintribe.gm.model.reason;

/**
 * UnsatisfiedMaybeTunneling is a vehicle to transport an unsatisfied Maybe {@link Reason} from methods that do not return a {@link Maybe} or other
 * structures that can hold a {@link Reason}. In that sense it is not there to communicate a real exception
 * but a structural error with meaning and potential expectation.
 *  
 * @author Dirk Scheffler
 */
public class UnsatisfiedMaybeTunneling extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Maybe<?> maybe;

	/**
	 * Constructs a ReasonException with no stacktrace.
	 * 
	 * @param maybe The {@link Maybe} to be transported
	 */
	public UnsatisfiedMaybeTunneling(Maybe<?> maybe) {
		super(maybe.whyUnsatisfied().getText(), null, false, false);
		this.maybe = maybe;
	}
	
	public <T> Maybe<T> getMaybe() {
		return (Maybe<T>) maybe;
	}
	
	public Reason whyUnsatisfied() {
		return maybe.whyUnsatisfied();
	}
	
	public static <T> T getOrTunnel(Maybe<T> maybe) {
		if (maybe.isUnsatisfied())
			throw new UnsatisfiedMaybeTunneling(maybe);
		
		return maybe.get();
	}
	
}

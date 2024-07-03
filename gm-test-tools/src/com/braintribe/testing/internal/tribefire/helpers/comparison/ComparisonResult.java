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
package com.braintribe.testing.internal.tribefire.helpers.comparison;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @deprecated this class was moved to {@link com.braintribe.model.processing.test.tools.comparison.ComparisonResult}
 */
@Deprecated
public class ComparisonResult {
	private final String message;
	private String customMessage;
	private final boolean areEqual;
	private final Object first, second;
	
	public ComparisonResult(String message, boolean areEqual, Object first, Object second) {
		this.message = message;
		this.areEqual = areEqual;
		this.first = first;
		this.second = second;
	}
	
	public boolean asBoolean() {
		return areEqual;
	}
	
	public String asDetailedMessage() {
		return introduction() + message;
	}
	
	public void assertThatEqual() {
		assertThat(areEqual).as(introduction() + "Error when comparing following objects: " + comparedObjectsDescription() + message).isTrue();
	}
	
	public void assertThatNotEqual() {
		assertThat(areEqual).as(introduction() + "Expected objects to be different but they were equal: " + comparedObjectsDescription()).isFalse();
	}
	
	public ComparisonResult withMessage(String customMessage) {
		this.customMessage = customMessage;
		return this;
	}
	
	private String comparedObjectsDescription() {
		return "\n  >" + first.toString() + "\n  >" + second.toString() + "\n";
	}
	
	private String introduction() {
		return (customMessage == null ? "" : customMessage) + "\n";
	}
}

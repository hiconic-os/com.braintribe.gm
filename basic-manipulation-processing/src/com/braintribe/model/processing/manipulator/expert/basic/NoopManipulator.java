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
package com.braintribe.model.processing.manipulator.expert.basic;

import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.processing.manipulator.api.Manipulator;
import com.braintribe.model.processing.manipulator.api.ManipulatorContext;

public class NoopManipulator<T extends Manipulation> implements Manipulator<T> {
	public static final NoopManipulator<Manipulation> defaultInstance = new NoopManipulator<Manipulation>();
	
	@SuppressWarnings("unchecked")
	public static <T1 extends Manipulation> Manipulator<T1> getCastedDefaultInstance() {
		return (Manipulator<T1>) defaultInstance;
	}
	
	@Override
	public void apply(T manipulation, ManipulatorContext context) {
		// do nothing
	}
}

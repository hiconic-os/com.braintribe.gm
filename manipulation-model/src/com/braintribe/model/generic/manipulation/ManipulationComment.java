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
package com.braintribe.model.generic.manipulation;

import java.util.Date;
import java.util.stream.Stream;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
public interface ManipulationComment extends VoidManipulation {

	EntityType<ManipulationComment> T = EntityTypes.T(ManipulationComment.class);

	String getAuthor();
	void setAuthor(String author);

	@Initializer("now()")
	Date getDate();
	void setDate(Date date);

	String getText();
	void setText(String text);

	@Override
	@SuppressWarnings("unusable-by-js")
	default Stream<AtomicManipulation> stream() {
		return Stream.of(this);
	}

}

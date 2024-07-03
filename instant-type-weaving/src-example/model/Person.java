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
package model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.Transient;

/**
 * @author peter.gazdik
 */
@SelectiveInformation("Prefix_${name}_${father.name}_${#id}_Suffix")
public interface Person extends GenericEntity {

	String getName();
	void setName(String value);

	long getCount();
	void setCount(long value);

	Person getFather();
	void setFather(Person value);

	@Transient
	String getTransientName();
	void setTransientName(String transientName);
}

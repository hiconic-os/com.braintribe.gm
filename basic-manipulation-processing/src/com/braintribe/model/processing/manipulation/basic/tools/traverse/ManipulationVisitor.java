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
package com.braintribe.model.processing.manipulation.basic.tools.traverse;

import com.braintribe.model.generic.manipulation.AbsentingManipulation;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.ManifestationManipulation;
import com.braintribe.model.generic.manipulation.RemoveManipulation;

/**
 * Used in combination with {@link ManipulationTraverser}.
 * 
 * @author peter.gazdik
 */
public interface ManipulationVisitor {

	void visit(InstantiationManipulation m);
	void visit(DeleteManipulation m);

	void visit(ChangeValueManipulation m);

	void visit(AddManipulation m);
	void visit(RemoveManipulation m);
	void visit(ClearCollectionManipulation m);

	void visit(ManifestationManipulation m);
	void visit(AbsentingManipulation m);

	/**
	 * @return true iff given {@link CompoundManipulation} should be traversed recursively
	 */
	boolean visit(CompoundManipulation cm);

}

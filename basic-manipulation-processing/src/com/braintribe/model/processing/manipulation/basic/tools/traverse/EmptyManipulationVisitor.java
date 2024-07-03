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
 * 
 */
public class EmptyManipulationVisitor implements ManipulationVisitor {

	@Override
	public void visit(InstantiationManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(DeleteManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(ChangeValueManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(AddManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(RemoveManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(ClearCollectionManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(ManifestationManipulation m) {
		// implement in sub-type
	}

	@Override
	public void visit(AbsentingManipulation m) {
		// implement in sub-type
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(CompoundManipulation cm) {
		return true;
	}

}

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

import com.braintribe.common.lcd.UnknownEnumException;
import com.braintribe.model.generic.manipulation.AbsentingManipulation;
import com.braintribe.model.generic.manipulation.AddManipulation;
import com.braintribe.model.generic.manipulation.ChangeValueManipulation;
import com.braintribe.model.generic.manipulation.ClearCollectionManipulation;
import com.braintribe.model.generic.manipulation.CompoundManipulation;
import com.braintribe.model.generic.manipulation.DeleteManipulation;
import com.braintribe.model.generic.manipulation.InstantiationManipulation;
import com.braintribe.model.generic.manipulation.ManifestationManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.RemoveManipulation;

/**
 * 
 * @author peter.gazdik
 */
public class ManipulationTraverser {

	public static void traverse(Manipulation m, ManipulationVisitor visitor) {
		switch (m.manipulationType()) {
			case COMPOUND: {
				CompoundManipulation cm = (CompoundManipulation) m;
				
				if (visitor.visit(cm)) {
					for (Manipulation mm: cm.getCompoundManipulationList()) {
						traverse(mm, visitor);
					}
				}
				return;
			}

			case ABSENTING:
				visitor.visit((AbsentingManipulation) m);
				return;
			case ADD:
				visitor.visit((AddManipulation) m);
				return;
			case CHANGE_VALUE:
				visitor.visit((ChangeValueManipulation) m);
				return;
			case CLEAR_COLLECTION:
				visitor.visit((ClearCollectionManipulation) m);
				return;
			case DELETE:
				visitor.visit((DeleteManipulation) m);
				return;
			case INSTANTIATION:
				visitor.visit((InstantiationManipulation) m);
				return;
			case MANIFESTATION:
				visitor.visit((ManifestationManipulation) m);
				return;
			case REMOVE:
				visitor.visit((RemoveManipulation) m);
				return;

			case VOID:
				// can be ignored
				return;

			default:
				throw new UnknownEnumException(m.manipulationType());
		}
	}

}

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
package com.braintribe.model.processing.workbench.experts;

import com.braintribe.model.processing.workbench.WorkbenchInstructionContext;
import com.braintribe.model.processing.workbench.WorkbenchInstructionExpert;
import com.braintribe.model.processing.workbench.WorkbenchInstructionProcessorException;
import com.braintribe.model.workbench.instruction.CompoundInstruction;
import com.braintribe.model.workbench.instruction.WorkbenchInstruction;

/**
 * A logical instruction expert that delegates it's elements to other experts.   
 */
public class CompoundInstructionExpert implements WorkbenchInstructionExpert<CompoundInstruction>{


	@Override
	public void process(CompoundInstruction compoundInstruction, WorkbenchInstructionContext context) throws WorkbenchInstructionProcessorException {
		for (WorkbenchInstruction instruction : compoundInstruction.getInstructions()) {
			WorkbenchInstructionExpert<WorkbenchInstruction> expert = context.getExpertForInstruction(instruction);
			expert.process(instruction, context);
		}
	}
	
	
}

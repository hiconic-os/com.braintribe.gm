// ============================================================================
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
// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.processing.manipulation.parser.impl.listener;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Set;

import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.DeleteManipulationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.InstanceAcquireContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.InstanceCreationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.InstanceLookupContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.QualifiedTypeAssignmentContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ValueAssignmentContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParserBaseListener;
import com.braintribe.utils.collection.api.IStack;
import com.braintribe.utils.collection.impl.ArrayStack;

/**
 * @author peter.gazdik
 */
public class HomeopathyDetectingParserListener extends GmmlParserBaseListener {

	public Set<String> createdVariableNames = newSet();
	public List<String> deletedVariableNames = newList();

	String lastVar;
	IStack<String> varStack = new ArrayStack<>();

	// ###############################################################
	// ## . . . . . . . . . . Variable push . . . . . . . . . . . . ##
	// ###############################################################

	@Override
	public void exitVariable(VariableContext ctx) {
		String varName = ctx.StandardIdentifier().getText();
		varStack.push(varName);
	}

	// ###############################################################
	// ## . . . . . . . . . . Variable pop . . . . . . . . . . . . .##
	// ###############################################################

	// variable EQ qualifiedType #qualifiedTypeAssignment

	@Override
	public void exitQualifiedTypeAssignment(QualifiedTypeAssignmentContext ctx) {
		pop();
	}

	// variable EQ assignableValue #valueAssignment

	@Override
	public void exitValueAssignment(ValueAssignmentContext ctx) {
		pop();
	}

	// variableValue: variable;

	@Override
	public void exitVariableValue(VariableValueContext ctx) {
		lastVar = pop();
	}

	// ##
	// ## Property Owner Assignment
	// ##

	// variable EQ (variableValue | LB variableAssignment RB | EXCLAMATION qualifiedType) LB RB # instanceCreation

	@Override
	public void exitInstanceCreation(InstanceCreationContext ctx) {
		createdVariableNames.add(pop());
	}

	// variable EQ (variableValue | LB variableAssignment RB | EXCLAMATION qualifiedType) LB stringValue RB #instanceLookup

	@Override
	public void exitInstanceLookup(InstanceLookupContext ctx) {
		pop();
	}

	// variable EQ (variableValue | LB variableAssignment RB | EXCLAMATION qualifiedType) LSB stringValue RSB #instanceAcquire

	@Override
	public void exitInstanceAcquire(InstanceAcquireContext ctx) {
		pop();
	}

	// ## Delete

	@Override
	public void exitDeleteManipulation(DeleteManipulationContext ctx) {
		deletedVariableNames.add(lastVar);
	}

	private String pop() {
		return varStack.pop();
	}

	public Set<String> homeopathincVariables() {
		deletedVariableNames.retainAll(createdVariableNames);
		return newSet(deletedVariableNames);
	}

}

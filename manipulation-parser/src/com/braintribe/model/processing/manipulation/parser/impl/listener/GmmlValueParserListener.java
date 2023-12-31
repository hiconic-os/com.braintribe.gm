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
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.AssignableValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.FullyQualifiedIdentifierContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ListElementContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ListValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.MapEntryContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.MapValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.SetElementContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.SetValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableAssignmentValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableOrAssignmentValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableValueContext;

public abstract class GmmlValueParserListener extends GmmlLiteralParserListener {

	protected boolean insideVariableAssignmentValue;

	@Override
	public void enterValue(ValueContext ctx) {
		// noop
	}

	@Override
	public void exitValue(ValueContext ctx) {
		// noop
	}

	@Override
	public void enterAssignableValue(AssignableValueContext ctx) {
		// noop
	}

	@Override
	public void exitAssignableValue(AssignableValueContext ctx) {
		// noop
	}

	@Override
	public void enterVariableValue(VariableValueContext ctx) {
		// noop
	}
	
	@Override
	public void enterVariableOrAssignmentValue(VariableOrAssignmentValueContext ctx) {
		// noop
	}

	@Override
	public void exitVariableOrAssignmentValue(VariableOrAssignmentValueContext ctx) {
		// noop
	}

	@Override
	public void exitVariableValue(VariableValueContext ctx) {
		String variableName = pop();
		Object variableValue = requireVariable(variableName);
		push(variableValue);
	}

	@Override
	public final void enterVariableAssignmentValue(VariableAssignmentValueContext ctx) {
		insideVariableAssignmentValue = true;
	}

	@Override
	public final void exitVariableAssignmentValue(VariableAssignmentValueContext ctx) {
		insideVariableAssignmentValue = false;
	}
	
	@Override
	public final void enterVariable(VariableContext ctx) {
		// noop
	}

	@Override
	public final void exitVariable(VariableContext ctx) {
		String variableName = ctx.StandardIdentifier().getText();
		push(variableName);
	}

	@Override
	public void enterFullyQualifiedIdentifier(FullyQualifiedIdentifierContext ctx) {
		// noop
	}

	@Override
	public void exitFullyQualifiedIdentifier(FullyQualifiedIdentifierContext ctx) {
		String qualifiedTypeName = ctx.StandardIdentifier().stream().map(TerminalNode::getText).collect(Collectors.joining("."));
		push(qualifiedTypeName);
	}

	@Override
	public void enterListElement(ListElementContext ctx) {
		// noop
	}

	@Override
	public void exitListElement(ListElementContext ctx) {
		Object element = pop();
		List<Object> list = peek();
		addToList(list, element);
	}

	protected void addToList(List<Object> list, Object element) {
		list.add(element);
	}

	@Override
	public void enterSetElement(SetElementContext ctx) {
		// noop
	}

	@Override
	public void exitSetElement(SetElementContext ctx) {
		Object element = pop();
		Set<Object> set = peek();
		addToSet(set, element);
	}

	protected void addToSet(Set<Object> set, Object element) {
		set.add(element);
	}

	@Override
	public void enterListValue(ListValueContext ctx) {
		push(newList());
	}

	@Override
	public void exitListValue(ListValueContext ctx) {
		// noop
	}

	@Override
	public void visitErrorNode(ErrorNode arg0) {
		// noop
	}

	@Override
	public void visitTerminal(TerminalNode arg0) {
		// noop
	}

	@Override
	public void enterSetValue(SetValueContext ctx) {
		push(newSet());
	}

	@Override
	public void exitSetValue(SetValueContext ctx) {
		// noop
	}

	@Override
	public void enterMapValue(MapValueContext ctx) {
		push(newMap());
	}

	@Override
	public void exitMapValue(MapValueContext ctx) {
		// noop
	}

	@Override
	public void enterMapEntry(MapEntryContext ctx) {
		// noop
	}

	@Override
	public void exitMapEntry(MapEntryContext ctx) {
		Object value = pop();
		Object key = pop();
		Map<Object, Object> map = peek();
		putToMap(map, key, value);
	}

	protected void putToMap(Map<Object, Object> map, Object key, Object value) {
		map.put(key, value);
	}

	protected abstract <T> T requireVariable(String name);

}

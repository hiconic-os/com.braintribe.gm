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
package com.braintribe.model.processing.manipulation.parser.impl.listener;

import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorParserConfiguration;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.AddContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.BooleanValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ChangeValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ClearContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.DateOffsetContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.DateValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.DecimalValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.DoubleValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EnumValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscBContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscBSContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscFContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscNContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscRContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscSQContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.EscTContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.FloatValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.IntegerDecimalRepresenationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.IntegerHexRepresentationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ListElementContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.ListValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.LongDecimalRepresenationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.LongHexRepresentationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.MapDeltaValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.MapEntryContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.MapValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.NullValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.PlainContentContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.PropertyManipulationContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.PropertyOwnerContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.RemoveContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.SetDeltaValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.SetElementContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.SetValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.SingleDeltaValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.StringValueContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.TimeZoneOffsetContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.UnicodeEscapeContext;
import com.braintribe.model.processing.manipulation.parser.impl.autogenerated.GmmlParser.VariableOrAssignmentValueContext;
import com.braintribe.model.processing.session.api.managed.EntityManager;

/**
 * @author peter.gazdik
 */
public class HomeopathyIgnoringManipulatorParserListener extends GmmlManipulatorParserListener {

	private final Set<String> deletedVariables;
	private final Set<GenericEntity> deletedEntities = newSet();

	private boolean ignorePropertyManipulation;

	public HomeopathyIgnoringManipulatorParserListener(EntityManager entityManager, GmmlManipulatorParserConfiguration configuration) {
		super(entityManager, configuration);

		this.deletedVariables = configuration.homeopathicVariables();
	}

	@Override
	protected void putVariableAndUpdateLastAssignment(String variableName, Object variableValue) {
		super.putVariableAndUpdateLastAssignment(variableName, variableValue);

		if (variableValue instanceof GenericEntity && deletedVariables.contains(variableName))
			deletedEntities.add((GenericEntity) variableValue);
	}

	// property manipulation

	@Override
	public void enterPropertyManipulation(PropertyManipulationContext ctx) {
		super.enterPropertyManipulation(ctx);
		checkPropertyOwner();
	}

	@Override
	public void exitPropertyOwner(PropertyOwnerContext ctx) {
		super.exitPropertyOwner(ctx);
		checkPropertyOwner();
	}
	
	private void checkPropertyOwner() {
		ignorePropertyManipulation = deletedEntities.contains(lastEntity);
	}

	@Override
	public void exitPropertyManipulation(PropertyManipulationContext ctx) {
		if (shouldParse())
			super.exitPropertyManipulation(ctx);
		else
			ignorePropertyManipulation = false;
	}

	// manipulationOperation: changeValue | add | remove | clear;

	@Override
	public void exitChangeValue(ChangeValueContext ctx) {
		if (shouldParse())
			super.exitChangeValue(ctx);
	}

	@Override
	public void exitAdd(AddContext ctx) {
		if (shouldParse())
			super.exitAdd(ctx);
	}

	@Override
	public void exitRemove(RemoveContext ctx) {
		if (shouldParse())
			super.exitRemove(ctx);
	}

	@Override
	public void exitClear(ClearContext ctx) {
		if (shouldParse())
			super.exitClear(ctx);
	}

	// DO NOTHING: singleDeltaValue | setDeltaValue | mapDeltaValue | listValue | setValue | mapValue | literalValue| nullValue

	// signle

	@Override
	public void exitSingleDeltaValue(SingleDeltaValueContext ctx) {
		if (shouldParse())
			super.exitSingleDeltaValue(ctx);
	}

	// collections

	@Override
	public void exitSetDeltaValue(SetDeltaValueContext ctx) {
		if (shouldParse())
			super.exitSetDeltaValue(ctx);
	}

	@Override
	public void exitMapDeltaValue(MapDeltaValueContext ctx) {
		if (shouldParse())
			super.exitMapDeltaValue(ctx);
	}

	@Override
	public void enterListValue(ListValueContext ctx) {
		if (shouldParse())
			super.enterListValue(ctx);
	}

	@Override
	public void enterSetValue(SetValueContext ctx) {
		if (shouldParse())
			super.enterSetValue(ctx);
	}

	@Override
	public void enterMapValue(MapValueContext ctx) {
		if (shouldParse())
			super.enterMapValue(ctx);
	}

	@Override
	public void exitListElement(ListElementContext ctx) {
		if (shouldParse())
			super.exitListElement(ctx);
	}

	@Override
	public void exitSetElement(SetElementContext ctx) {
		if (shouldParse())
			super.exitSetElement(ctx);
	}

	@Override
	public void exitMapEntry(MapEntryContext ctx) {
		if (shouldParse())
			super.exitMapEntry(ctx);
	}

	// literals
	// booleanValue
	// | floatValue
	// | doubleValue
	// | decimalValue
	// | integerValue
	// | longValue
	// | stringValue
	// | dateValue
	// | enumValue

	@Override
	public void enterDateValue(DateValueContext ctx) {
		if (shouldParse())
			super.enterDateValue(ctx);
	}
	@Override
	public void exitDateValue(DateValueContext ctx) {
		if (shouldParse())
			super.exitDateValue(ctx);
	}

	@Override
	public void exitDateOffset(DateOffsetContext ctx) {
		if (shouldParse())
			super.exitDateOffset(ctx);
	}

	@Override
	public void exitTimeZoneOffset(TimeZoneOffsetContext ctx) {
		if (shouldParse())
			super.exitTimeZoneOffset(ctx);
	}

	@Override
	public void exitBooleanValue(BooleanValueContext ctx) {
		if (shouldParse())
			super.exitBooleanValue(ctx);
	}

	@Override
	public void enterStringValue(StringValueContext ctx) {
		if (shouldParse())
			super.enterStringValue(ctx);
	}

	@Override
	public void exitStringValue(StringValueContext ctx) {
		if (shouldParse())
			super.exitStringValue(ctx);
	}

	@Override
	public void exitPlainContent(PlainContentContext ctx) {
		if (shouldParse())
			super.exitPlainContent(ctx);
	}

	@Override
	public void exitEscB(EscBContext ctx) {
		if (shouldParse())
			super.exitEscB(ctx);
	}

	@Override
	public void exitEscBS(EscBSContext ctx) {
		if (shouldParse())
			super.exitEscBS(ctx);
	}

	@Override
	public void exitEscF(EscFContext ctx) {
		if (shouldParse())
			super.exitEscF(ctx);
	}

	@Override
	public void exitEscN(EscNContext ctx) {
		if (shouldParse())
			super.exitEscN(ctx);
	}

	@Override
	public void exitEscR(EscRContext ctx) {
		if (shouldParse())
			super.exitEscR(ctx);
	}

	@Override
	public void exitEscSQ(EscSQContext ctx) {
		if (shouldParse())
			super.exitEscSQ(ctx);
	}

	@Override
	public void exitEscT(EscTContext ctx) {
		if (shouldParse())
			super.exitEscT(ctx);
	}

	@Override
	public void exitUnicodeEscape(UnicodeEscapeContext ctx) {
		if (shouldParse())
			super.exitUnicodeEscape(ctx);
	}

	@Override
	public void exitDecimalValue(DecimalValueContext ctx) {
		if (shouldParse())
			super.exitDecimalValue(ctx);
	}

	@Override
	public void exitFloatValue(FloatValueContext ctx) {
		if (shouldParse())
			super.exitFloatValue(ctx);
	}

	@Override
	public void exitDoubleValue(DoubleValueContext ctx) {
		if (shouldParse())
			super.exitDoubleValue(ctx);
	}

	@Override
	public void exitLongDecimalRepresenation(LongDecimalRepresenationContext ctx) {
		if (shouldParse())
			super.exitLongDecimalRepresenation(ctx);
	}

	@Override
	public void exitLongHexRepresentation(LongHexRepresentationContext ctx) {
		if (shouldParse())
			super.exitLongHexRepresentation(ctx);
	}

	@Override
	public void exitIntegerDecimalRepresenation(IntegerDecimalRepresenationContext ctx) {
		if (shouldParse())
			super.exitIntegerDecimalRepresenation(ctx);
	}

	@Override
	public void exitIntegerHexRepresentation(IntegerHexRepresentationContext ctx) {
		if (shouldParse())
			super.exitIntegerHexRepresentation(ctx);
	}

	// enum

	@Override
	public void exitEnumValue(EnumValueContext ctx) {
		if (shouldParse())
			super.exitEnumValue(ctx);
	}

	// null

	@Override
	public void exitNullValue(NullValueContext ctx) {
		if (shouldParse())
			super.exitNullValue(ctx);
	}

	// POP FROM STACK: variableOrAssignmentValue

	@Override
	public void exitVariableOrAssignmentValue(VariableOrAssignmentValueContext ctx) {
		if (shouldParse())
			super.exitVariableOrAssignmentValue(ctx);
		else
			pop();
	}

	private boolean shouldParse() {
		return !ignorePropertyManipulation || insideVariableAssignmentValue;
	}

}

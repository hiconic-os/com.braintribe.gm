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
package com.braintribe.model.processing.traversing.engine.impl.skip.conditional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.model.generic.typecondition.TypeConditions;
import com.braintribe.model.generic.typecondition.basic.IsTypeKind;
import com.braintribe.model.generic.typecondition.basic.TypeKind;
import com.braintribe.model.mpc.ModelPathCondition;
import com.braintribe.model.mpc.logic.MpcJunctionCapture;
import com.braintribe.model.mpc.value.MpcElementAxis;
import com.braintribe.model.processing.mpc.MPC;
import com.braintribe.model.processing.mpc.builder.api.MpcBuilder;
import com.braintribe.testing.category.KnownIssue;

/**
 * Tests for {@link MpcConfigurableSkipper}
 */
@Category(KnownIssue.class)
public class MpcConfigurableSkipperTest extends AbstractSkipperTest {

	static final IsTypeKind isListType = TypeConditions.isKind(TypeKind.listType);

	MpcConfigurableSkipper skipper;
	MpcBuilder $;

	@Before
	public void setup() {
		skipper = new MpcConfigurableSkipper();
		$ = MPC.builder();
	}
	
	@Test
	public void  testPropertyMatchListItem() throws Exception {
		ModelPathCondition condition = $.listItem();
		
		skipper.setCondition(condition);
		
		propertyMatchListItem(skipper);
	}
	
	@Test
	public void  testPropertyNameMatch() throws Exception {
		ModelPathCondition condition = $.property("someB");
		
		skipper.setCondition(condition);
		
		propertyNameMatch(skipper);
	}
	
	@Test
	public void  testMatchesTypeOfMatch() throws Exception {
		ModelPathCondition condition = $.matchesType(MpcElementAxis.value, isListType);
		
		skipper.setCondition(condition);
		
		matchesTypeOfMatch(skipper);
	}
	
	@Test
	public void  testNegationMatch() throws Exception {
		ModelPathCondition condition = $.negation(
											$.root()
										);
		
		skipper.setCondition(condition);
		
		negationMatch(skipper);
	}
	
	@Test
	public void  testSequenceMatch() throws Exception {
		ModelPathCondition condition = $.sequenceWithoutCapture($.root(),
																$.property("someB")
															);
		
		skipper.setCondition(condition);
		
		sequenceMatch(skipper);
	}
	
	@Test
	public void  testDisjunctionMatch() throws Exception {
		ModelPathCondition condition = $.disjunction(MpcJunctionCapture.none,
											$.property("enumA"),
											$.property("someB")
										);
		
		skipper.setCondition(condition);
		
		disjunctionMatch(skipper);
	}
		
	@Test
	public void  testConjunctionMatch() throws Exception {
		ModelPathCondition condition = $.conjunction(MpcJunctionCapture.none,
											$.matchesType(MpcElementAxis.value, isListType),
											$.property("list")
										);
		
		skipper.setCondition(condition);
		
		conjunctionMatch(skipper);
	}
	
}

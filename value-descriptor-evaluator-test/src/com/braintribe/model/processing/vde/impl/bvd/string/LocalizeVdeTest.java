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
package com.braintribe.model.processing.vde.impl.bvd.string;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.braintribe.model.bvd.string.Localize;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.processing.vde.evaluator.api.VdeRuntimeException;

public class LocalizeVdeTest extends AbstractStringVdeTest {

	@Test
	public void testExistingLocaleLocalized() throws Exception{
		Localize stringFunction = $.localize();
		
		LocalizedString localisedString = LocalizedString.T.create();
		localisedString.setLocalizedValues(getLocaleMap());
		
		stringFunction.setLocalizedString(localisedString);
		stringFunction.setLocale("DE");
		
		Object result = evaluate(stringFunction);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(String.class);
		assertThat(result).isEqualTo("Hallo");
	}
	
	@Test
	public void testNonExistingLocaleLocalized() throws Exception{
		Localize stringFunction = $.localize();
		
		LocalizedString localisedString = LocalizedString.T.create();
		localisedString.setLocalizedValues(getLocaleMap());
		
		stringFunction.setLocalizedString(localisedString);
		stringFunction.setLocale("FR");
		
		Object result = evaluate(stringFunction);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(String.class);
		assertThat(result).isEqualTo("Hi");
	}
	
	@Test (expected=VdeRuntimeException.class)
	public void testRandomLocalizedStringOperandLocalized() throws Exception{
		Localize stringFunction = $.localize();
		stringFunction.setLocalizedString(new Date());
		
		evaluate(stringFunction);
	}
	
	@Test (expected=VdeRuntimeException.class)
	public void testRandomLocaleOperandLocalized() throws Exception{
		Localize stringFunction = $.localize();
		LocalizedString localisedString = LocalizedString.T.create();
		localisedString.setLocalizedValues(getLocaleMap());
		stringFunction.setLocalizedString(localisedString);
		stringFunction.setLocale(new Date());
		
		evaluate(stringFunction);
	}
	
	private Map<String,String> getLocaleMap(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("EN", "Hello");
		map.put("DE", "Hallo");
		map.put("default", "Hi");
		return map;
	}

}

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
package com.braintribe.model.processing.template.building.test;

import java.util.Date;

import org.junit.Test;

import com.braintribe.model.bvd.math.Subtract;
import com.braintribe.model.bvd.time.Now;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.meta.data.prompt.Name;
import com.braintribe.model.processing.template.building.impl.Templates;
import com.braintribe.model.processing.template.building.test.model.Person;
import com.braintribe.model.processing.vde.builder.api.VdBuilder;
import com.braintribe.model.processing.vde.evaluator.VDE;
import com.braintribe.model.processing.vde.evaluator.api.aspects.DateAspect;
import com.braintribe.model.template.Template;
import com.braintribe.model.time.TimeUnit;
import com.braintribe.utils.i18n.I18nTools;

/**
 * 
 * @author Dirk Scheffler
 *
 */
public class TemplateBuildingTest {

	@Test
	public void testTemplateBuilding() throws Exception {
		Template template = Templates
		.template(I18nTools.createLs("template"))
		.prototype(c -> {
			return c.create(Person.T);
		})
		.record(c -> {
			Person prototype = c.getPrototype();

			Name name = Name.T.create();
			name.setName(I18nTools.createLs("template"));
			
			c.pushVariable("name").addMetaData(name);
			prototype.setName("unkown");
			
			VdBuilder $ = VDE.builder();
			Subtract sub = $.subtract(Now.T.create(), $.timeSpan(1, TimeUnit.hour));
			
			c.pushVd(sub);
			prototype.setBirthday(null);
		})
		.build();
		
		Object prototype = template.getPrototype();
		Manipulation script = template.getScript();
		
		System.out.println(prototype);
		
	
		
		
	}
	
	@Test
	public void testVd() {
		VdBuilder $ = VDE.builder();
		Subtract sub = $.subtract($.now(), $.timeSpan(1, TimeUnit.hour), $.timeSpan(3, TimeUnit.hour));
		
		Object value = VDE.evaluate(sub);
		
		System.out.println(value);
	}


}

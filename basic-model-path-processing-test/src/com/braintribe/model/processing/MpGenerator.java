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
package com.braintribe.model.processing;

import java.util.ArrayList;
import java.util.List;

import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.processing.misc.model.Person;
import com.braintribe.model.processing.mp.builder.impl.MpBuilderImpl;

/**
 * Generator for the different Model path elements used for the testing of the MPC
 *
 */
public class MpGenerator {

	/**
	 * @return a {@link IModelPathElement} that represents a simple model path. It uses {@link Person} as the root. The
	 *         path is "Root.name"
	 * 
	 */
	public static IModelPathElement getSimplePath() {

		// Person p = (Person) GMF.getTypeReflection().getEntityType(Person.class).newInstance();
		Person p = Person.T.create();
		p.setName("My Name");

		return new MpBuilderImpl().root(p).property("name").build();
	}
	
	//TODO Fix me
	public static IModelPathElement getSimpleReptitivePath() {

		Person p1 = Person.T.create();
		Person p2 = Person.T.create();
		Person p3 = Person.T.create();
		Person p4 = Person.T.create();
		Person p5 = Person.T.create();
		Person p6 = Person.T.create();
		p1.setName("p1");
		p1.setName("p2");
		p1.setName("p3");
		p1.setName("p4");
		p1.setName("p5");
		p1.setName("p6");
		
		p1.setDescendant(p2);
		p2.setDescendant(p3);
		p3.setDescendant(p4);
		p4.setDescendant(p5);
		p5.setDescendant(p6);
		
		return new MpBuilderImpl().root(p1).property("descendant").property("descendant").property("descendant").build();
	}
	
	

	/**
	 * @return a {@link IModelPathElement} that represents a model path with three elements. It uses {@link Person} as
	 *         the root. The path is "Root.favouriteColours[1]"
	 * 
	 */
	public static IModelPathElement getTernaryPath() {

		Person p = Person.T.create();
		List<String> colours = new ArrayList<String>();
		colours.add("blue");
		colours.add("red");
		p.setFavouriteColours(colours);

		return new MpBuilderImpl().root(p).property("favouriteColours").listItem(1).build();
	}

}

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
package com.braintribe.model.processing.vde.impl.bvd.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.Test;

import com.braintribe.model.bvd.context.ModelPath;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.ModelPathElement;
import com.braintribe.model.generic.path.RootPathElement;
import com.braintribe.model.processing.vde.evaluator.api.aspects.RootModelPathAspect;
import com.braintribe.model.processing.vde.evaluator.api.aspects.SelectedModelPathsAspect;
import com.braintribe.model.processing.vde.impl.VDGenerator;
import com.braintribe.model.processing.vde.test.VdeTest;
import com.braintribe.model.user.User;

public class ModelPathVdeTest extends VdeTest {

	public static VDGenerator $ = new VDGenerator();

	@Test
	public void testModelPathFirstElement() throws Exception {
		ModelPath modelPathVd = $.modelPathFirstElement();

		User u1 = user("Foo1");
		User u2 = user("Foo2");

		Object result = evaluateWith(
				RootModelPathAspect.class, buildModelPath(u1,u2), modelPathVd);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isEqualTo(u1);
	}
	
	@Test
	public void testModelPathLastElement() throws Exception {
		ModelPath modelPathVd = $.modelPathLastElement();

		User u1 = user("Foo1");
		User u2 = user("Foo2");

		Object result = evaluateWith(
				RootModelPathAspect.class, buildModelPath(u1,u2), modelPathVd);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isEqualTo(u2);
	}
	
	@Test
	public void testModelPathFirstOffsetElement() throws Exception {
		ModelPath modelPathVd = $.modelPathFirstElementOffset(1);

		User u1 = user("Foo1");
		User u2 = user("Foo2");

		Object result = evaluateWith(
				RootModelPathAspect.class, buildModelPath(u1,u2), modelPathVd);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isEqualTo(u2);
	}

	@Test
	public void testModelPathLastOffsetElement() throws Exception {
		ModelPath modelPathVd = $.modelPathLastElementOffset(1);

		User u1 = user("Foo1");
		User u2 = user("Foo2");

		Object result = evaluateWith(
				RootModelPathAspect.class, buildModelPath(u1,u2), modelPathVd);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isEqualTo(u1);
	}

	@Test
	public void testModelPathFirstElementSelection() throws Exception {
		ModelPath modelPathVd = $.modelPathFirstElementForSelection();

		User u1 = user("Foo1");
		User u2 = user("Foo2");


		Object result = evaluateWith(
				SelectedModelPathsAspect.class, Collections.singletonList(buildModelPath(u1,u2)), modelPathVd);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isEqualTo(u1);
	}

	
	@Test
	public void testModelPathLastElementSelection() throws Exception {
		ModelPath modelPathVd = $.modelPathLastElementForSelection();

		User u1 = user("Foo1");
		User u2 = user("Foo2");


		Object result = evaluateWith(
				SelectedModelPathsAspect.class, Collections.singletonList(buildModelPath(u1,u2)), modelPathVd);
		
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(User.class);
		assertThat(result).isEqualTo(u2);
	}
	
	private com.braintribe.model.generic.path.ModelPath buildModelPath(GenericEntity... arr) {
		com.braintribe.model.generic.path.ModelPath modelPath = new com.braintribe.model.generic.path.ModelPath();
		for (GenericEntity e : arr) {
			ModelPathElement element = new RootPathElement(e);
			modelPath.add(element);
		}
		return modelPath;
	}

	private User user(String name) {
		User u = User.T.create();
		u.setName(name);
		return u;
	}

}

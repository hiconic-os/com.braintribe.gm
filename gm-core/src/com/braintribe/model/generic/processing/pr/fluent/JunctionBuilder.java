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
package com.braintribe.model.generic.processing.pr.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.braintribe.model.generic.pr.criteria.JunctionCriterion;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.EntityType;

public class JunctionBuilder<T> extends CriterionBuilder<JunctionBuilder<T>>{
	@SuppressWarnings("hiding")
	private final T backLink;
	private final List<TraversingCriterion> criteria = new ArrayList<TraversingCriterion>();
	@SuppressWarnings("hiding")
	private final Consumer<? super JunctionCriterion> receiver;
	private final EntityType<? extends JunctionCriterion> junctionType;
	
	public JunctionBuilder(EntityType<? extends JunctionCriterion> junctionType, T backLink, Consumer<? super JunctionCriterion> receiver) {
		this.receiver = receiver;
		this.backLink = backLink;
		this.junctionType = junctionType;
		setBackLink(this);
		setReceiver(new Consumer<TraversingCriterion>() {
			@Override
			public void accept(TraversingCriterion criterion) throws RuntimeException {
				criteria.add(criterion);
			}
		});
	}
	
	public T close() {
		JunctionCriterion junction = junctionType.create();
		junction.setCriteria(criteria);
		receiver.accept(junction);
		return backLink;
	}
	

}

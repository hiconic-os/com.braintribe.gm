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
package com.braintribe.model.processing.meta.cmd.context;

import static com.braintribe.model.processing.meta.cmd.tools.CmdTools.asString;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.braintribe.model.processing.meta.cmd.CascadingMetaDataException;
import com.braintribe.model.processing.meta.oracle.ModelOracle;

/** Immutable implementation of {@link SelectorContext}. */
public class StaticContext implements SelectorContext {

	Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues = new HashMap<Class<? extends SelectorContextAspect<?>>, Object>();

	public static final StaticContext EMPTY_CONTEXT = new StaticContext((ModelOracle) null,
			Collections.<Class<? extends SelectorContextAspect<?>>, Object> emptyMap());

	private final ModelOracle modelOracle;

	public StaticContext(ModelOracle modelOracle, Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues) {
		validate(aspectValues);
		this.modelOracle = modelOracle;
		this.aspectValues.putAll(aspectValues);
	}

	public StaticContext(StaticContext parent, Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues) {
		validate(aspectValues);
		this.modelOracle = parent.getModelOracle();
		this.aspectValues.putAll(parent.aspectValues);
		this.aspectValues.putAll(aspectValues);
	}

	private void validate(Map<Class<? extends SelectorContextAspect<?>>, Object> aspectValues) {
		List<Class<? extends SelectorContextAspect<?>>> nullAspects = newList();

		for (Entry<Class<? extends SelectorContextAspect<?>>, Object> entry : aspectValues.entrySet())
			if (entry.getValue() == null)
				nullAspects.add(entry.getKey());

		if (!nullAspects.isEmpty())
			throw new CascadingMetaDataException("Value of an aspect cannot be null. Null aspects: " + asString(nullAspects));
	}

	@Override
	public ModelOracle getModelOracle() {
		return modelOracle;
	}

	@Override
	public <T, A extends SelectorContextAspect<T>> T getNotNull(Class<A> aspect) {
		T value = get(aspect);
		if (value == null)
			throw new CascadingMetaDataException("Aspect not found in the context: " + aspect.getName());

		return value;
	}

	@Override
	public <T, A extends SelectorContextAspect<T>> T get(Class<A> aspect) {
		return (T) aspectValues.get(aspect);
	}

	public void addTo(MutableSelectorContext selectorContext) {
		for (Entry<Class<? extends SelectorContextAspect<?>>, Object> entry : aspectValues.entrySet()) {
			Class<? extends SelectorContextAspect<Object>> key = (Class<? extends SelectorContextAspect<Object>>) entry.getKey();
			Object value = entry.getValue();

			selectorContext.put(key, value);
		}
	}

	public boolean containsAllAspects(Collection<Class<? extends SelectorContextAspect<?>>> aspects) {
		return aspectValues.keySet().containsAll(aspects);
	}
}

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
package com.braintribe.model.processing.meta.cmd.context;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.selector.MetaDataSelector;
import com.braintribe.model.meta.selector.UseCaseSelector;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.builders.MdResolver;
import com.braintribe.model.processing.meta.cmd.context.experts.CmdSelectorExpert;
import com.braintribe.model.processing.meta.cmd.context.experts.SelectorExpert;

/**
 * A base for {@link CmdSelectorExpert} for logical {@link MetaDataSelector}s.
 * <p>
 * One special aspect of logical selectors is they are automatically not-ignored in case we resolve with
 * {@link MdResolver#ignoreSelectorsExcept(EntityType...)} option.
 * 
 * @see #maybeMatches(MetaDataSelector, SelectorContext)
 */
public interface LogicalSelectorExpert<T extends MetaDataSelector> extends CmdSelectorExpert<T> {

	/**
	 * Similar to {@link SelectorExpert#matches(MetaDataSelector, SelectorContext)}, but may return {@code null} which indicates the answer is not
	 * clear as some relevant {@link MetaDataSelector} was ignored. MD for which this method returns {@code null} or {@code true} is resolved, because
	 * only {@code false} returned from this method indicates the MD is not resolvable.
	 * <p>
	 * For example when ignoring all selectors except for {@link UseCaseSelector}, any MD with a non-matching use-case will be ignored, while all MD
	 * with matching use-case are considered.
	 * <p>
	 * See part about caching in the description of {@link CmdResolver}.
	 */
	Boolean maybeMatches(T selector, SelectorContext context) throws Exception;

}

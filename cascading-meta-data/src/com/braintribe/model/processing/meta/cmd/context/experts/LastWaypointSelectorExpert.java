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
package com.braintribe.model.processing.meta.cmd.context.experts;

import java.util.Collection;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.common.Waypoint;
import com.braintribe.model.meta.selector.LastWaypointSelector;
import com.braintribe.model.processing.meta.cmd.context.SelectorContext;
import com.braintribe.model.processing.meta.cmd.context.SelectorContextAspect;
import com.braintribe.model.processing.meta.cmd.context.aspects.UseCaseAspect;
import com.braintribe.model.processing.meta.cmd.tools.MetaDataTools;
import com.braintribe.utils.collection.impl.AttributeContexts;

/** The selector is active iff its waypoint equals the {@link Waypoint} on the {@link AttributeContext}. */
public class LastWaypointSelectorExpert implements CmdSelectorExpert<LastWaypointSelector> {

	@Override
	public Collection<Class<? extends SelectorContextAspect<?>>> getRelevantAspects(LastWaypointSelector selector) throws Exception {
		// This is not technically correct, but it informs the MD resolver this selector is volatile and cannot be cached, which is what we need
		return MetaDataTools.aspects(UseCaseAspect.class);
	}

	@Override
	public boolean matches(LastWaypointSelector selector, SelectorContext context) throws Exception {
		String waypoint = AttributeContexts.peek().findOrNull(Waypoint.class);
		if (waypoint == null)
			return false;

		String selectorWaypoint = selector.getWaypoint();

		return waypoint.equals(selectorWaypoint);
	}

}

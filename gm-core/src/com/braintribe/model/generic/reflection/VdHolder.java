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
package com.braintribe.model.generic.reflection;

import java.util.Objects;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.value.ValueDescriptor;

/**
 * This is (intended to be) used as a wrapper for a {@link ValueDescriptor} in case that is used instead of an actual
 * property value of an entity.
 * 
 * This is currently an experimental feature which we are planning to introduce later, and is not yet supported.
 * 
 * IMPORTANT: This class must remain final, as we are checking if something is {@link VdHolder} by doing
 * {@Code object.getClass() == VdHolder.class}.
 * 
 * @author peter.gazdik
 */
public final class VdHolder {

	public final ValueDescriptor vd;
	public final boolean isAbsenceInformation;

	public static final VdHolder standardAiHolder = new VdHolder(GMF.absenceInformation());

	public VdHolder(ValueDescriptor vd) {
		this.vd = Objects.requireNonNull(vd, "ValueDescriptor cannot be null!");
		this.isAbsenceInformation = vd instanceof AbsenceInformation;
	}

	public static VdHolder newInstance(ValueDescriptor vd) {
		return vd == GMF.absenceInformation() ? VdHolder.standardAiHolder : new VdHolder(vd);
	}

	public static AbsenceInformation getAbsenceInfoIfPossible(Object o) {
		if (!isVdHolder(o))
			return null;

		if (o == standardAiHolder)
			return (AbsenceInformation) standardAiHolder.vd;

		VdHolder vh = (VdHolder) o;
		return vh.isAbsenceInformation ? (AbsenceInformation) vh.vd : null;
	}

	public static ValueDescriptor getValueDescriptorIfPossible(Object o) {
		return isVdHolder(o) ? ((VdHolder) o).vd : null;
	}

	public static <T> T getValueIfPossible(Object o) {
		return (T) (isVdHolder(o) ? null : o);
	}

	public static boolean isAbsenceInfo(Object o) {
		return isVdHolder(o) && //
				(o == standardAiHolder || ((VdHolder) o).isAbsenceInformation);
	}

	public static boolean isVdHolder(Object o) {
		return o != null && o.getClass() == VdHolder.class;
	}

	public static void checkIsAbsenceInfo(Object vdHolder, GenericEntity entity, Property property) {
		VdHolder vh = (VdHolder) vdHolder;
		if (!(vh.isAbsenceInformation))
			throw new UnsupportedOperationException("Feature not supported. Cannot process ValueDescriptor other than AbsenceInformation. Property '"
					+ property.getName() + "' of: " + entity + ". ValueDescriptor: " + vh.vd);
	}

	@Override
	public String toString() {
		return "VD (" + vd.getClass().getSimpleName() + ")";
	}

}

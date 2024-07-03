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
package com.braintribe.model.acl;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.Unmodifiable;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import jsinterop.annotations.JsMethod;

/**
 * <p>
 * Acl stands for Access Control List whose elements instances of {@link AclEntry}. The overall effect of the Acl is the accumulated effect of the
 * single entries whereby the entries with a {@link AclPermission#DENY} permission are interpreted with priority over any entry with a
 * {@link AclPermission#GRANT} for the same operation.
 * 
 * Acl instances are used on and shared amongst {@link HasAcl} instances to control access to them.
 * 
 * @see HasAcl
 * @see AclEntry
 * 
 * @author Dirk Scheffler
 */
@SelectiveInformation("ACL: ${name}")
public interface Acl extends GenericEntity {

	EntityType<Acl> T = EntityTypes.T(Acl.class);

	String name = "name";
	String entries = "entries";
	String accessibility = "accessibility";

	/**
	 * The mandatory human readable name of the Acl for a better recognition
	 */
	@Mandatory
	String getName();
	void setName(String name);

	/**
	 * The list of {@link AclEntry} that controls operational rights in fine-grained way. The order of the list has no influence on the determination
	 * of actual permissions and just stabilizes the presentation.
	 */
	List<AclEntry> getEntries();
	void setEntries(List<AclEntry> entries);

	/**
	 * <p>
	 * This property is automatically being calculated to hold an excerpt from the {@link #getEntries()} that is relevant to efficiently enrich
	 * queries.
	 * <p>
	 * It can hold roles and denied roles which are expressed with a "!" prefix.
	 */
	@Unmodifiable
	Set<String> getAccessibility();
	void setAccessibility(Set<String> accessibility);

	/**
	 * Checks whether given roles are allowed to perform given operation.
	 * <p>
	 * The result is based on the configured {@link #getEntries() entries} - if there is at least one matching granting entry and there is no matching
	 * denying entry, than the operation is granted. Matching entry is one that exactly matches given operation and one of the given roles.
	 * 
	 * @see HasAcl#isOperationGranted(String, String, Set)
	 * 
	 * @param operation
	 *            the operation, expressed as a string, that is being authorized
	 * @param roles
	 *            the roles that are used to check access rights
	 * 
	 * @return true if the access is granted
	 */
	@JsMethod
	default boolean isOperationGranted(String operation, Set<String> roles) {
		boolean granted = false;

		for (AclEntry entry : getEntries()) {
			if (operation.equals(entry.operation()) && roles.contains(entry.getRole())) {
				switch (entry.getPermission()) {
					case GRANT:
						granted = true;
						break;

					case DENY:
						return false;

					default:
						break;
				}
			}
		}

		return granted;
	}

	/**
	 * Convenient access check for {@link AclOperation standard ACL operations}. Calling this method is equivalent to
	 * {@code isOperationGranted(operation.name(), roles)}.
	 * 
	 * @return true if the access is granted.
	 */
	@JsMethod(name="isAclOperationGranted")
	default boolean isOperationGranted(AclOperation operation, Set<String> roles) {
		return isOperationGranted(operation.name(), roles);
	}
}

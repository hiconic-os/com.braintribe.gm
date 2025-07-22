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
package com.braintribe.model.security.service.config;

import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Description("Configures the SecurityService to apply an authorization rule for a given entry point."
        + " The rule is to specifically allow or forbid certain roles to enter via this entry point.")
public interface OpenUserSessionEntryPoint extends GenericEntity {

    EntityType<OpenUserSessionEntryPoint> T = EntityTypes.T(OpenUserSessionEntryPoint.class);

    String name = "name";
    String activationWaypoints = "activationWaypoints";
    String allowedRoles = "allowedRoles";
    String forbiddenRoles = "forbiddenRoles";

    @Description("The name of the entry point to be used in direct addressing.")
    String getName();
    void setName(String name);

    @Description("The names of all waypoints that will activate this entry point.")
    Set<String> getActivationWaypoints();
    void setActivationWaypoints(Set<String> activationWaypoints);

    @Description("The roles that are explicitly allowed to access this entry point. "
            + "If this set is not empty, the user must have at least one of these roles. "
            + "Ignored if the user has any role listed in forbiddenRoles.")
    Set<String> getAllowedRoles();
    void setAllowedRoles(Set<String> allowedRoles);

    @Description("The roles that are explicitly forbidden from accessing this entry point. "
            + "If the user has any of these roles, access is denied, regardless of allowedRoles.")
    Set<String> getForbiddenRoles();
    void setForbiddenRoles(Set<String> forbiddenRoles);
} 

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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface HttpRequestSelector extends GenericEntity {

    EntityType<HttpRequestSelector> T = EntityTypes.T(HttpRequestSelector.class);

    String origin = "origin";
    String host = "host";

    @Description("HTTP origin correlated with the HTTP header Origin.")
    @Initializer("'*'")
    @Mandatory
    String getOrigin();
    void setOrigin(String origin);
    
    @Description("HTTP host correlated with the HTTP header Host/X-Forwarded-Host.")
    @Initializer("'*'")
    @Mandatory
    String getHost();
    void setHost(String host);
} 

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
package com.braintribe.model.example;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("${street} ${zip} ${place}")

public interface Address extends StandardIdentifiable {

	EntityType<Address> T = EntityTypes.T(Address.class);

	String street = "street";
	String place = "place";
	String zip = "zip";
	String city = "city";
	String country = "country";

	String getStreet();
	void setStreet(String street);

	String getPlace();
	void setPlace(String place);

	String getZip();
	void setZip(String zip);

	String getCity();
	void setCity(String city);

	String getCountry();
	void setCountry(String country);

}

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
package com.braintribe.gm.model.usersession;

import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Indexed;
import com.braintribe.model.generic.annotation.meta.MaxLength;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface PersistenceUserSession extends GenericEntity {

	EntityType<PersistenceUserSession> T = EntityTypes.T(PersistenceUserSession.class);

	String acquirationKey = "acquirationKey";
	String userName = "userName";
	String userFirstName = "userFirstName";
	String userLastName = "userLastName";
	String userEmail = "userEmail";
	String creationDate = "creationDate";
	String fixedExpiryDate = "fixedExpiryDate";
	String expiryDate = "expiryDate";
	String lastAccessedDate = "lastAccessedDate";
	String maxIdleTime = "maxIdleTime";
	String effectiveRoles = "effectiveRoles";
	String sessionType = "sessionType";
	String creationInternetAddress = "creationInternetAddress";
	String creationNodeId = "creationNodeId";
	String properties = "properties";
	String blocksAuthenticationAfterLogout = "blocksAuthenticationAfterLogout";
	String closed = "closed";

	@Indexed
	String getAcquirationKey();
	void setAcquirationKey(String acquirationKey);

	Boolean getBlocksAuthenticationAfterLogout();
	void setBlocksAuthenticationAfterLogout(Boolean blocksAuthenticationAfterLogout);

	Boolean getClosed();
	void setClosed(Boolean closed);

	String getUserName();
	void setUserName(String name);

	String getUserFirstName();
	void setUserFirstName(String firstName);

	String getUserLastName();
	void setUserLastName(String lastName);

	String getUserEmail();
	void setUserEmail(String email);

	Date getCreationDate();
	void setCreationDate(Date creationDate);

	@Indexed
	Date getFixedExpiryDate();
	void setFixedExpiryDate(Date fixedExpiryDate);

	/**
	 * Expiry date is calculated on each access (touch) by adding the {@code maxIdleTime} to the {@code lastAccessedDate}.
	 * If {@code fixedExpiryDate} comes before the mentioned sum, {@code expiryDate} is set to be equal to
	 * {@code fixedExpiryDate}.
	 */
	@Indexed
	Date getExpiryDate();
	void setExpiryDate(Date expiryDate);

	Date getLastAccessedDate();
	void setLastAccessedDate(Date lastAccessedDate);

	/**
	 * Expressed in milliseconds.
	 */
	Long getMaxIdleTime();
	void setMaxIdleTime(Long maxIdleTime);

	@MaxLength(4000)
	String getEffectiveRoles();
	void setEffectiveRoles(String effectiveRoles);

	String getSessionType();
	void setSessionType(String sessionType);

	@MaxLength(1000)
	String getCreationInternetAddress();
	void setCreationInternetAddress(String creationInternetAddress);

	@MaxLength(1000)
	String getCreationNodeId();
	void setCreationNodeId(String creationNodeId);

	@MaxLength(4000)
	String getProperties();
	void setProperties(String properties);
	
	default boolean closed() {
		Boolean closed = getClosed();
		if (closed == null)
			return false;
		
		return closed;
	}
	
	default boolean blocksAuthenticationAfterLogout() {
		Boolean blocksAuthenticationAfterLogout = getBlocksAuthenticationAfterLogout();
		if (blocksAuthenticationAfterLogout == null) 
			return false;
		
		return blocksAuthenticationAfterLogout;
	}

}

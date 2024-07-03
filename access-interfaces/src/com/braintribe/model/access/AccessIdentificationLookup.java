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
package com.braintribe.model.access;

/**
 * access to the access identification : 
 * 
 * enables to identify an {@link IncrementalAccess} access and to retrieve the access again by its id  
 * 
 * @author pit
 * @author dirk
 *
 */
public interface AccessIdentificationLookup {

	/**
	 * @param access - the access we want to id of 
	 * @return - the id of the access 
	 */
	public String lookupAccessId( IncrementalAccess access);
	
	/**
	 * @param id - the id of the access 
	 * @return - returns the top level access of the access with this id
	 */
	public IncrementalAccess lookupAccess( String id);
}

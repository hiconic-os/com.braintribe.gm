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
package com.braintribe.codec.marshaller.json.model;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface AccountAccess extends GenericEntity {
	
	EntityType<AccountAccess> T = EntityTypes.T(AccountAccess.class);
	
	String 	getAvailableAccounts();
	void   	setAvailableAccounts(String value);
	
	String 	getAllPsd2();
	void 	setAllPsd2(String value);
	
	String 	getAvailableAccountsWithBalances();
	void	setAvailableAccountsWithBalances(String value);
	
	List<AccountReference> getBalances();
	void 	setBalances(List<AccountReference> value);
	
	List<AccountReference> getAccounts();
	void 	setAccounts(List<AccountReference> value);
	
	List<AccountReference> getTransactions();
	void 	setTransactions(List<AccountReference> value);

}

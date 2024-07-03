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
package com.braintribe.model.processing.rpc.test.wire.space;

import com.braintribe.model.crypto.key.KeyPair;
import com.braintribe.model.processing.rpc.test.commons.TestClientKeyProvider;
import com.braintribe.model.processing.securityservice.commons.service.InMemorySecurityServiceProcessor;
import com.braintribe.model.user.User;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

@Managed
public class CommonsSpace implements WireSpace {

	@Import
	private CommonsSpace commons;

	@Import
	private CryptoSpace crypto;

	@Import
	private ServerCommonsSpace serverCommons;

	@Managed
	public InMemorySecurityServiceProcessor securityProcessor() {
		InMemorySecurityServiceProcessor bean = new InMemorySecurityServiceProcessor();
		User user1 = User.T.create();
		user1.setName("testuser");
		user1.setPassword("testuser");
		bean.addUser(user1);
		return bean;
	}

	// <bean id="rpc.crypto.clientKeyProvider"
	// class="com.braintribe.model.processing.rpc.test.commons.TestClientKeyProvider">
	// <property name="cryptorFactory" ref="crypto.cipherCryptorFactory" />
	// <property name="keyPairGenerator" ref="crypto.standardKeyGenerator.keyPair" />
	// <property name="keyPairSpec">
	// <bean class="com.braintribe.model.crypto.key.KeyPair">
	// <property name="keyAlgorithm" value="RSA" />
	// </bean>
	// </property>
	// </bean>
	@Managed
	public TestClientKeyProvider clientKeyProvider() {
		TestClientKeyProvider bean = new TestClientKeyProvider();
		bean.setCryptorFactory(crypto.cipherCryptorFactory());
		bean.setKeyPairGenerator(crypto.standardKeyPairGenerator());
		bean.setKeyPairSpec(clientKeySpec());
		return bean;
	}

	@Managed
	public KeyPair clientKeySpec() {
		KeyPair bean = KeyPair.T.create();
		bean.setKeyAlgorithm("RSA");
		return bean;
	}

}

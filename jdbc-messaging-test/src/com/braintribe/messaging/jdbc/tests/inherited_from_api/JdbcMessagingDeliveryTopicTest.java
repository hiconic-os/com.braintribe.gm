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
package com.braintribe.messaging.jdbc.tests.inherited_from_api;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.messaging.jdbc.tests.JdbcMessagingInstance;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.test.GmMessagingDeliveryTopicTest;

/**
 * This requires a running Postgres DB, see {@link DbTestDataSourcesContract#postgres()}
 * 
 * @author peter.gazdik
 */
@Category(SpecialEnvironment.class)
public class JdbcMessagingDeliveryTopicTest extends GmMessagingDeliveryTopicTest {

	private static JdbcMessagingInstance msgInstance;

	@BeforeClass
	public static void setup() {
		msgInstance = new JdbcMessagingInstance();
	}

	@AfterClass
	public static void tearDown() {
		msgInstance.shutDown();
	}

	@Override
	protected MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		return msgInstance.connectionProvider;
	}

	@Override
	protected MessagingContext getMessagingContext() {
		return msgInstance.messagingContext;
	}

}

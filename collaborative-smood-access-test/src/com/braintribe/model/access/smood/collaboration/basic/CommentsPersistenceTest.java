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
package com.braintribe.model.access.smood.collaboration.basic;

import org.junit.Test;

import com.braintribe.model.access.collaboration.CollaborativeAccessManager;
import com.braintribe.model.access.smood.collaboration.manager.model.StagedEntity;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

/**
 * Not really related to the {@link CollaborativeAccessManager}, here we test whether the comments are persisted in the
 * manipulation files with CSA.
 * 
 * @author peter.gazdik
 */
public class CommentsPersistenceTest extends AbstractCollaborativePersistenceTest {

	// TODO add comment author
	@Test
	public void commitWithComment() throws Exception {
		final String COMMENT = "TestCommitComment";

		session.create(StagedEntity.T);
		session.prepareCommit().comment(COMMENT).commit();

		String s = getTrunkFileContent(false);

		Assertions.assertThat(s).contains("text:'" + COMMENT + "'");
	}

}

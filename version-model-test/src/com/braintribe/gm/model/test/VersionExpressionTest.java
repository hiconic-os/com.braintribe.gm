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
package com.braintribe.gm.model.test;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.version.FuzzyVersion;
import com.braintribe.model.version.Version;
import com.braintribe.model.version.VersionExpression;
import com.braintribe.model.version.VersionIntervals;
import com.braintribe.model.version.VersionRange;

public class VersionExpressionTest extends AbstractVersionTest{

	@Test
	public void test() {
		AssertingConsumer aCt = new AssertingConsumer();
	
		VersionExpression ve = VersionExpression.parse( "1.0", aCt);
		Assert.assertTrue( "expected [version], found [" + ve.getClass().getSimpleName(), ve instanceof Version);
		aCt.assertState(true, true, true);
		
		ve = VersionExpression.parse( "[1.0]", aCt);
		Assert.assertTrue( "expected [version], found [" + ve.getClass().getSimpleName(), ve instanceof Version);
		aCt.assertState(true, false, true);
		
		ve = VersionExpression.parse( "(1.0,]");
		Assert.assertTrue( "expected [VersionRange], found [" + ve.getClass().getSimpleName(), ve instanceof VersionRange);
		aCt.assertState(false, null, true);
		
		ve = VersionExpression.parse( "[1.0,1.1)", aCt);
		Assert.assertTrue( "expected [FuzzyVersion], found [" + ve.getClass().getSimpleName(), ve instanceof FuzzyVersion);
		aCt.assertState(false, null, true);
		
		ve = VersionExpression.parse( "[1.0,1.2)", aCt);
		Assert.assertTrue( "expected [VersionRange], found [" + ve.getClass().getSimpleName(), ve instanceof VersionRange);
		aCt.assertState(false, null, true);
		
		ve = VersionExpression.parse( "[1.1.0,1.2.1)", aCt);
		Assert.assertTrue( "expected [VersionRange], found [" + ve.getClass().getSimpleName(), ve instanceof VersionRange);
		aCt.assertState(false, null, true);
		
		
		ve = VersionExpression.parse("(,1.0],[1.2,)");
		Assert.assertTrue( "expected [VersionIntervals], found [" + ve.getClass().getSimpleName(), ve instanceof VersionIntervals);
		aCt.assertState(false, null, true);
	}


	/**
	 * helper class to test the 'non negotiable version' notification 
	 * @author pit
	 *
	 */
	private class AssertingConsumer implements Consumer<Boolean> {
		private boolean called;
		private Boolean result;
				
		public void reset() {
			called = false;
			result = null;
		}

		@Override
		public void accept(Boolean t) {
			called = true;
			result = t;
		}
		
		public void assertState( boolean called, Boolean result, boolean reset) {
			Assert.assertTrue("expected called ["+ called + "], but found called [" + this.called + "]", called == this.called);
			if (this.result == null) {
				if (result != null) {
					Assert.fail("result [" + result  +"] expected, but nothing found") ;
				}
				else {
					return;
				}
				
			}
			else {				
				Assert.assertTrue("expected result ["+ result + "], but found called [" + this.result + "]", called == this.called);
			}
			if (reset) {
				reset();
			}
		}
		
	}

}

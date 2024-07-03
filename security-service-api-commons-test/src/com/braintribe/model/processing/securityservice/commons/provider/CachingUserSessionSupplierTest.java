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
package com.braintribe.model.processing.securityservice.commons.provider;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.braintribe.model.time.TimeSpan;
import com.braintribe.model.time.TimeUnit;
import com.braintribe.model.usersession.UserSession;

public class CachingUserSessionSupplierTest {

	@Test
	public void testCaching() {

		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSession);

		UserSession usA = supplier.get();
		UserSession usB = supplier.get();

		Assert.assertEquals(usA, usB);

	}

	@Test
	public void testRenewalByInvalidation() {

		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSession);

		UserSession usA = supplier.get();

		supplier.accept("anything");

		UserSession usB = supplier.get();

		Assert.assertNotEquals(usA, usB);

	}

	@Test
	public void testCachingUponRenewalByInvalidation() {

		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSession);

		UserSession usA = supplier.get();
		UserSession usB = supplier.get();

		Assert.assertEquals(usA, usB);

		supplier.accept("anything");

		UserSession usC = supplier.get();
		UserSession usD = supplier.get();

		Assert.assertNotEquals(usA, usC);
		Assert.assertEquals(usC, usD);

	}

	@Test
	public void testDisabledPurgeAgeFactor() throws Exception {

		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSessionWithExpiryDate);

		UserSession usA = supplier.get();
		UserSession usB = supplier.get();

		Assert.assertEquals(usA, usB);

		// Above expiration, won't trigger purge as no purgeAgeFactor was set.
		Thread.sleep(1000);

		UserSession usC = supplier.get();
		UserSession usD = supplier.get();

		Assert.assertEquals(usA, usC);
		Assert.assertEquals(usC, usD);

	}

	@Test
	public void testRenewalByPurgeAgeFactorBasedOnExpiryDate() throws Exception {
		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSessionWithExpiryDate);
		supplier.setPurgeAgeFactor(0.5f);
		testRenewalUponReachedTimeToLive(supplier);
	}

	@Test
	public void testRenewalByPurgeAgeFactorBasedOnMaxIdleTime() throws Exception {
		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSessionWithMaxIdleTime);
		supplier.setPurgeAgeFactor(0.5f);
		testRenewalUponReachedTimeToLive(supplier);

	}

	@Test
	public void testRenewalByPurgeAgeFactorBasedOnExpiryDatePrecedence() throws Exception {
		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSessionWithPrioritaryExpiryDate);
		supplier.setPurgeAgeFactor(0.5f);
		testRenewalUponReachedTimeToLive(supplier);
	}

	@Test
	public void testRenewalByPurgeAgeFactorBasedOnMaxIdleTimePrecedence() throws Exception {
		CachingUserSessionSupplier supplier = new CachingUserSessionSupplier();
		supplier.setAuthenticator(this::newSessionWithPrioritaryMaxIdleTime);
		supplier.setPurgeAgeFactor(0.5f);
		testRenewalUponReachedTimeToLive(supplier);
	}

	protected void testRenewalUponReachedTimeToLive(CachingUserSessionSupplier supplier) throws Exception {

		UserSession usA = supplier.get();
		UserSession usB = supplier.get();

		Assert.assertNotNull(usA);
		Assert.assertNotNull(usB);

		Assert.assertEquals(usA, usB);

		// As the factor is 0.5 and the sessions have the max age of 1 sec, waiting for 600ms should be enough to
		// trigger the purge:
		Thread.sleep(600);

		UserSession usC = supplier.get();
		UserSession usD = supplier.get();

		Assert.assertNotNull(usC);
		Assert.assertNotNull(usD);

		Assert.assertNotEquals(usA, usC);
		Assert.assertEquals(usC, usD);

	}

	private UserSession newSession() {
		UserSession s = UserSession.T.create();
		s.setSessionId(UUID.randomUUID().toString());
		return s;
	}

	private UserSession newSessionWithMaxIdleTime() {
		UserSession s = newSession();
		s.setMaxIdleTime(newTimeSpan(TimeUnit.second, 1));
		return s;
	}

	private UserSession newSessionWithExpiryDate() {
		UserSession s = UserSession.T.create();
		s.setExpiryDate(new Date(System.currentTimeMillis() + 1000));
		return s;
	}

	private UserSession newSessionWithPrioritaryMaxIdleTime() {
		UserSession s = newSession();
		s.setMaxIdleTime(newTimeSpan(TimeUnit.second, 1));
		s.setExpiryDate(new Date(System.currentTimeMillis() + 2000));
		return s;
	}

	private UserSession newSessionWithPrioritaryExpiryDate() {
		UserSession s = UserSession.T.create();
		s.setExpiryDate(new Date(System.currentTimeMillis() + 1000));
		s.setMaxIdleTime(newTimeSpan(TimeUnit.second, 2));
		return s;
	}

	private TimeSpan newTimeSpan(TimeUnit unit, double value) {
		TimeSpan ts = TimeSpan.T.create();
		ts.setUnit(unit);
		ts.setValue(value);
		return ts;
	}

}

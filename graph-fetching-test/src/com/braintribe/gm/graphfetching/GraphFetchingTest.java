package com.braintribe.gm.graphfetching;

import org.junit.Ignore;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.testing.tools.gm.GmTestTools;

@Ignore
public class GraphFetchingTest extends AbstractGraphFetchingTest {

	@Override
	protected boolean generateIds() {
		return true;
	}

	@Override
	protected IncrementalAccess buildAccess() {
		return GmTestTools.newSmoodAccessMemoryOnly(ACCESS_ID_TEST, model);
	}

	@Override
	public void testPolymorphism() {
		super.testPolymorphism();
	}
}

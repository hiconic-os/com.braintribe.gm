package com.braintribe.gm.graphfetching;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.testing.tools.gm.GmTestTools;

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

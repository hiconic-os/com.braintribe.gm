package com.braintribe.gm.initializer.jdbc.test.wire.contract;

import com.braintribe.common.db.DbVendor;
import com.braintribe.gm.initializer.jdbc.processing.GmDbInitializerManager;
import com.braintribe.wire.api.space.WireSpace;

/**
 * @author peter.gazdik
 */
public interface InitializerManagerTestContract extends WireSpace {

	String TABLE_NAME = "hc_init_tasks";

	GmDbInitializerManager newManager(DbVendor vendor);

}

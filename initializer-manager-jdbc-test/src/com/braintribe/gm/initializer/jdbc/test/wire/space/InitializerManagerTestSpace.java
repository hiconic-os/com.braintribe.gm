package com.braintribe.gm.initializer.jdbc.test.wire.space;

import com.braintribe.common.db.DbVendor;
import com.braintribe.common.db.wire.contract.DbTestDataSourcesContract;
import com.braintribe.gm.initializer.jdbc.processing.GmDbInitializerManager;
import com.braintribe.gm.initializer.jdbc.test.wire.contract.InitializerManagerTestContract;
import com.braintribe.model.processing.lock.impl.SimpleCdlLocking;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

/**
 * @author peter.gazdik
 */
@Managed
public class InitializerManagerTestSpace implements InitializerManagerTestContract {

	@Import
	private DbTestDataSourcesContract dataSources;

	@Override
	public GmDbInitializerManager newManager(DbVendor vendor) {
		GmDbInitializerManager bean = new GmDbInitializerManager();
		bean.setDataSource(dataSources.dataSource(vendor));
		bean.setTasksTableName(TABLE_NAME);
		bean.setUseCase("test");
		bean.setNodeId("test-node");
		bean.setLocking(new SimpleCdlLocking());
		return bean;
	}

}

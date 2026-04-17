package com.braintribe.gm.initializer.jdbc.test.wire;

import java.util.List;

import com.braintribe.common.db.wire.DbTestConnectionsWireModule;
import com.braintribe.gm.initializer.jdbc.test.wire.contract.InitializerManagerTestContract;
import com.braintribe.wire.api.module.WireModule;
import com.braintribe.wire.api.module.WireTerminalModule;

/**
 * @author peter.gazdik
 */
public enum InitializerManagerTestWireModule implements WireTerminalModule<InitializerManagerTestContract> {

	INSTANCE;

	@Override
	public List<WireModule> dependencies() {
		return List.of(DbTestConnectionsWireModule.INSTANCE);
	}

}

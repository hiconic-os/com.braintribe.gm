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
package com.braintribe.util.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.braintribe.utils.lcd.LazyInitialization;

/**
 * DataSource which ensures the DB schema before it acquires a JDBC {@link Connection} from given delegate.
 * 
 * @author peter.gazdik
 */
public abstract class SchemaEnsuringDataSource implements DataSource {

	protected DataSource delegate;

	private final LazyInitialization schemaUpdate = new LazyInitialization(this::updateSchema);

	protected abstract void updateSchema();

	public void setDelegate(DataSource delegate) {
		this.delegate = delegate;
	}

	// DB access - Schema Update Required

	@Override
	public Connection getConnection() throws SQLException {
		schemaUpdate.run();
		return delegate.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		schemaUpdate.run();
		return delegate.getConnection(username, password);
	}

	// Beyond Java 8

	// @Override
	// public ConnectionBuilder createConnectionBuilder() throws SQLException {
	// schemaUpdate.run();
	// return delegate.createConnectionBuilder();
	// }
	//
	// @Override
	// public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
	// schemaUpdate.run();
	// return delegate.createShardingKeyBuilder();
	// }

	// NO DB access - Schema Update NOT Required

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return delegate.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return delegate.isWrapperFor(iface);
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return delegate.getParentLogger();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}

}

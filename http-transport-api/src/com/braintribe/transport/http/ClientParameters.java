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
package com.braintribe.transport.http;

public class ClientParameters {

	protected Integer maxTotal = null;
	protected Integer maxPerRoute = null;
	protected Integer socketTimeout = null;
	protected Boolean tracePooling = null;
	protected Long poolTimeToLive = null;
	protected Long validateAfterInactivity = null;
	protected Long maxIdleConnectionTtl = null;
	
	public static final ClientParameters emptyParameters = new ClientParameters();
	
	public ClientParameters() {
		//Nothing to do
	}

	public ClientParameters(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public Integer getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}
	public Integer getMaxPerRoute() {
		return maxPerRoute;
	}
	public void setMaxPerRoute(Integer maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}
	public Integer getSocketTimeout() {
		return socketTimeout;
	}
	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	public Boolean getTracePooling() {
		return tracePooling;
	}
	public void setTracePooling(Boolean tracePooling) {
		this.tracePooling = tracePooling;
	}
	public Long getPoolTimeToLive() {
		return poolTimeToLive;
	}
	public void setPoolTimeToLive(Long poolTimeToLive) {
		this.poolTimeToLive = poolTimeToLive;
	}
	public Long getValidateAfterInactivity() {
		return validateAfterInactivity;
	}
	public void setValidateAfterInactivity(Long validateAfterInactivity) {
		this.validateAfterInactivity = validateAfterInactivity;
	}
	public Long getMaxIdleConnectionTtl() {
		return maxIdleConnectionTtl;
	}
	public void setMaxIdleConnectionTtl(Long maxIdleConnectionTtl) {
		this.maxIdleConnectionTtl = maxIdleConnectionTtl;
	}

	@Override
	public String toString() {
		return "ClientParameters: maxTotal: "+maxTotal+", maxPerRoute: "+maxPerRoute+", socketTimeout: "+socketTimeout+", tracePooling: "+tracePooling+", poolTimeToLive: "+poolTimeToLive+", validateAfterInactivity: "+validateAfterInactivity;
	}
}

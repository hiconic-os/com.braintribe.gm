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
package com.braintribe.model.processing.bootstrapping.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.braintribe.logging.Logger;

public class TribefireRuntimeMBeanTools {

	private static Logger logger = Logger.getLogger(TribefireRuntimeMBeanTools.class);
			
	private static Map<String,TribefireRuntimeMBean> tribefireCartrigdeRuntimes = new ConcurrentHashMap<>();

	public static final String tribefireRuntimeMBeanPrefix = "com.braintribe.tribefire:type=TribefireRuntime,name=";

	public static TribefireRuntimeMBean getTribefireCartridgeRuntime(String cartridgeName) {
		return tribefireCartrigdeRuntimes.computeIfAbsent(cartridgeName, c -> {
			try {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				String cartridgeRuntimeName = String.format("%s%s", tribefireRuntimeMBeanPrefix, c);

				TribefireRuntimeMBean bean = null;

				ObjectName as = new ObjectName(cartridgeRuntimeName);
				if (mbs.isRegistered(as)) {
					// Get TribefireRuntime of Cartridge-MBean
					bean = JMX.newMBeanProxy(mbs, as, TribefireRuntimeMBean.class);
				}
				return bean; 
			} catch (MalformedObjectNameException e) {
				logger.error(String.format("Invalid TribefireCartrigdeRuntime name: %s", c), e);
				return null;
			}
		});
	}
}

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
package com.braintribe.gm.service.access.test.wire;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.List;

import com.braintribe.gm.service.access.test.wire.contract.AccessRequestProcessingTestContract;
import com.braintribe.gm.service.access.wire.common.CommonAccessProcessingWireModule;
import com.braintribe.gm.service.wire.common.CommonServiceProcessingWireModule;
import com.braintribe.wire.api.module.WireModule;
import com.braintribe.wire.api.module.WireTerminalModule;

public enum AccessRequestProcessingTestWireModule implements WireTerminalModule<AccessRequestProcessingTestContract> {
	INSTANCE;
	
	@Override
	public List<WireModule> dependencies() {
		return asList(CommonServiceProcessingWireModule.INSTANCE, CommonAccessProcessingWireModule.INSTANCE);
	}
	
}

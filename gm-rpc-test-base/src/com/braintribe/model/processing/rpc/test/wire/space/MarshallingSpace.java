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
package com.braintribe.model.processing.rpc.test.wire.space;

import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.codec.marshaller.api.ConfigurableMarshallerRegistry;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.codec.marshaller.common.BasicConfigurableMarshallerRegistry;
import com.braintribe.codec.marshaller.jse.JseMarshaller;
import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.codec.marshaller.stax.StaxMarshaller;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

@Managed
public class MarshallingSpace implements WireSpace {

	@Managed
	public ConfigurableMarshallerRegistry registry() {

		BasicConfigurableMarshallerRegistry bean = new BasicConfigurableMarshallerRegistry();

		bean.registerMarshaller("application/xml", xmlMarshaller());
		bean.registerMarshaller("text/xml", xmlMarshaller());
		bean.registerMarshaller("gm/xml", xmlMarshaller());

		bean.registerMarshaller("application/gm", binMarshaller());
		bean.registerMarshaller("gm/bin", binMarshaller());

		bean.registerMarshaller("application/json", jsonMarshaller());
		bean.registerMarshaller("text/x-json", jsonMarshaller());
		bean.registerMarshaller("gm/json", jsonMarshaller());

		bean.registerMarshaller("gm/jseh", jseMarshaller());
		bean.registerMarshaller("gm/jse", jseScriptMarshaller());

		return bean;

	}

	@Managed
	public Marshaller xmlMarshaller() {
		StaxMarshaller bean = new StaxMarshaller();
		bean.setRequiredTypesReceiver(requiredTypeEnsurer());
		return bean;
	}

	@Managed
	public Marshaller jsonMarshaller() {
		JsonStreamMarshaller bean = new JsonStreamMarshaller();
		return bean;
	}

	@Managed
	public Marshaller binMarshaller() {
		Bin2Marshaller bean = new Bin2Marshaller();
		bean.setRequiredTypesReceiver(requiredTypeEnsurer());
		return bean;
	}

	@Managed
	public Marshaller jseMarshaller() {
		JseMarshaller bean = new JseMarshaller();
		bean.setHostedMode(true);
		return bean;
	}

	@Managed
	public Marshaller jseScriptMarshaller() {
		JseMarshaller bean = new JseMarshaller();
		bean.setHostedMode(false);
		return bean;
	}

	private Consumer<Set<String>> requiredTypeEnsurer() {
		return o -> {
			/* no-op */
		};
	}

}

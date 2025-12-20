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
package com.braintribe.model.manipulation.parser.impl.manipulator;

import static com.braintribe.utils.SysPrint.spOut;
import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.Before;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.manipulation.parser.impl.model.GmmlTestModel;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.manipulation.marshaller.LocalManipulationStringifier;
import com.braintribe.model.processing.manipulation.marshaller.ManipulationStringifier;
import com.braintribe.model.processing.manipulation.marshaller.RemoteManipulationStringifier;
import com.braintribe.model.processing.manipulation.parser.api.GmmlManipulatorErrorHandler;
import com.braintribe.model.processing.manipulation.parser.api.MutableGmmlManipulatorParserConfiguration;
import com.braintribe.model.processing.manipulation.parser.impl.Gmml;
import com.braintribe.model.processing.manipulation.parser.impl.ManipulatorParser;
import com.braintribe.model.processing.manipulation.parser.impl.listener.error.StrictErrorHandler;
import com.braintribe.model.processing.session.api.managed.EntityManager;
import com.braintribe.model.processing.smood.Smood;
import com.braintribe.model.processing.test.tools.meta.ManipulationDriver;
import com.braintribe.model.processing.test.tools.meta.ManipulationDriver.SessionRunnable;
import com.braintribe.model.processing.test.tools.meta.ManipulationTrackingMode;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * @author peter.gazdik
 */
public class AbstractManipulatorTest {

	protected static final GmMetaModel metaModel = GmmlTestModel.raw();

	private static ManipulationTrackingMode manipulationMode = ManipulationTrackingMode.LOCAL;

	protected Smood smood;
	protected EntityManager session;

	protected Manipulation recorededManipulation;
	protected String manipulationString;

	@Before
	public void setup() throws Exception {
		smood = GmTestTools.newSmood(metaModel);
		session = (EntityManager) smood.getGmSession();

		// just to be sure, in case someone else is playing with this test
		if (manipulationMode == ManipulationTrackingMode.PERSISTENT)
			throw new RuntimeException("Wrong test setup. PERSISTENT manipulation mode is not supported.");
	}

	/**
	 * THIS IS THE CORE METHOD FOR TESTING
	 * <p>
	 * It does the following:
	 * <ol>
	 * <li>Records the manipulations.</li>
	 * <li>Prepares a string version of them, including any needed adjustments</li>
	 * <li>Applies them using {@link ManipulatorParser} (see {@link #parseAndApply()})</li>
	 * </ol>
	 */
	protected void recordStringifyAndApply(SessionRunnable sr) throws Exception {
		record(sr);
		prepareManipulationAsString();
		parseAndApply();
	}

	// for debugging
	protected static void print(GenericEntity entity) {
		System.out.println(entity.entityType().getShortName() + "(" + entity.getGlobalId() + ")");

		for (Property property : entity.entityType().getProperties()) {
			if (property.isGlobalId())
				continue;

			Object value = property.get(entity);
			if (value == null)
				continue;

			if (value instanceof Collection && isEmpty((Collection<?>) value))
				continue;

			if (value instanceof Map && isEmpty((Map<?, ?>) value))
				continue;

			System.out.println(property.getName() + " -> " + value);
		}
	}

	protected void record(SessionRunnable r) {
		ManipulationDriver driver = new ManipulationDriver();
		driver.setTrackingMode(manipulationMode);
		recorededManipulation = driver.dryRun(r);
	}

	protected void prepareManipulationAsString() throws IOException {
		StringBuilder sb = new StringBuilder();

		ManipulationStringifier stringifier = isGlobal() ? new RemoteManipulationStringifier() : new LocalManipulationStringifier();
		stringifier.setSingleBlock(true);
		stringifier.stringify(sb, recorededManipulation);
		manipulationString = processManipulationString(sb.toString());
		spOut(2, "-->");
		spOut(manipulationString);
	}

	// Override in lenient case to alter the manipulations
	protected String processManipulationString(String string) {
		return string;
	}

	private boolean isGlobal() {
		return manipulationMode == ManipulationTrackingMode.GLOBAL;
	}

	protected void parseAndApply() {
		ManipulatorParser.parse(manipulationString, session, parserConfig());
	}

	protected MutableGmmlManipulatorParserConfiguration parserConfig() {
		MutableGmmlManipulatorParserConfiguration result = Gmml.manipulatorConfiguration();
		result.setParseSingleBlock(true);
		result.setErrorHandler(errorHandler());
		return result;
	}

	protected GmmlManipulatorErrorHandler errorHandler() {
		return StrictErrorHandler.INSTANCE;
	}

}

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
package com.braintribe.model.access.collaboration.persistence.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.processing.manipulation.parser.api.MutableGmmlManipulatorParserConfiguration;
import com.braintribe.model.processing.manipulation.parser.api.ParseResponse;
import com.braintribe.model.processing.manipulation.parser.impl.Gmml;
import com.braintribe.model.processing.manipulation.parser.impl.ManipulationParser;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.utils.FileTools;

/**
 * @author peter.gazdik
 */
public class CsaPersistenceTools {

	public static final String TRUNK_STAGE = "trunk";

	/* The threshold value should be examined more, the initial 4 MB is just a guess of what is safe to load in memory. Roman allegedly had huge
	 * problems with a file of about 50 MB, so I guess 4 could still be OK. */
	private static final long MEGA_BYTE = 1024 * 1024;
	private static final long SMALL_FILE_SIZE_THRESHOLD = 4 * MEGA_BYTE;
	private static final long HOMEOPATHY_IGNORING_THRESHOLD = 100 * MEGA_BYTE;

	public static ParseResponse parseGmmlFile(File gmmlFile, Consumer<? super AtomicManipulation> manipulationConsumer) {
		return FileTools.read(gmmlFile).fromReader( //
				r -> ManipulationParser.parse(r, manipulationConsumer, parserConfig(gmmlFile)));
	}

	public static MutableGmmlManipulatorParserConfiguration parserConfig(File gmmlFile) {
		MutableGmmlManipulatorParserConfiguration result = Gmml.manipulatorConfiguration();
		result.setBufferEntireInput(isSmallFile(gmmlFile));

		return result;
	}

	private static boolean isSmallFile(File gmmlFile) {
		return gmmlFile.length() < SMALL_FILE_SIZE_THRESHOLD;
	}

	public static Set<String> resolveHomeopathicVariables(File gmmlFile) {
		if (!isBigEnoughToResolveHomeopathicVariables(gmmlFile))
			return Collections.emptySet();

		try (InputStream in = new FileInputStream(gmmlFile)) {
			return ManipulationParser.findHomeopathicVariables(new InputStreamReader(in), parserConfig(gmmlFile));
		} catch (Exception e) {
			throw new ManipulationPersistenceException("Error while processing GMML file: " + gmmlFile.getAbsolutePath(), e);
		}
	}

	private static boolean isBigEnoughToResolveHomeopathicVariables(File gmmlFile) {
		return gmmlFile.length() >= HOMEOPATHY_IGNORING_THRESHOLD;
	}

}

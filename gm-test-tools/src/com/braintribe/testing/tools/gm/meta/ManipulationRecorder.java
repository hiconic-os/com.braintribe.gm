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
package com.braintribe.testing.tools.gm.meta;

import static com.braintribe.model.processing.manipulation.basic.tools.ManipulationTools.asManipulation;
import static com.braintribe.testing.tools.gm.meta.ManipulationRecordingMode.GLOBAL;
import static com.braintribe.testing.tools.gm.meta.ManipulationRecordingMode.LOCAL;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;
import java.util.function.Consumer;

import com.braintribe.model.access.IncrementalAccess;
import com.braintribe.model.generic.manipulation.AtomicManipulation;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.ManipulationType;
import com.braintribe.model.generic.tracking.ManipulationListener;
import com.braintribe.model.processing.manipulation.basic.tools.ManipulationRemotifier;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.processing.session.impl.persistence.BasicPersistenceGmSession;

/**
 * @author peter.gazdik
 */
public class ManipulationRecorder {

	private IncrementalAccess access;
	private ManipulationRecordingMode recordingMode;

	public ManipulationRecorder withAccess(IncrementalAccess access) {
		this.access = access;
		return this;
	}

	public ManipulationRecorder local() {
		return mode(ManipulationRecordingMode.LOCAL);
	}

	public ManipulationRecorder persistent() {
		return mode(ManipulationRecordingMode.PERSISTENT);
	}

	public ManipulationRecorder global() {
		return mode(ManipulationRecordingMode.GLOBAL);
	}

	private ManipulationRecorder mode(ManipulationRecordingMode mode) {
		this.recordingMode = mode;
		return this;
	}

	public ManipulationRecorderResult record(Consumer<PersistenceGmSession> blockToRecord) {
		return trackHelper(blockToRecord, newPersistenceSession(), false);
	}

	private ManipulationRecorderResult trackHelper(Consumer<PersistenceGmSession> blockToRecord, PersistenceGmSession session, boolean undo) {
		blockToRecord.accept(session);

		List<Manipulation> manipulations = session.getTransaction().getManipulationsDone();

		if (undo)
			session.getTransaction().undo(manipulations.size());

		Manipulation manipulation = asManipulation(manipulations);

		manipulation = remotifyIfEligible(manipulation);
		
		return new ManipulationRecorderResult(manipulation);
	}

	private Manipulation remotifyIfEligible(Manipulation cm) {
		if (recordingMode != LOCAL)
			return ManipulationRemotifier.with(cm).globalReferences(recordingMode == GLOBAL).remotify();
		else
			return cm;
	}

	private PersistenceGmSession newPersistenceSession() {
		return new BasicPersistenceGmSession(access);
	}

	// ###################################################
	// ## . . . . . . Manipulation Listener . . . . . . ##
	// ###################################################

	static class SimpleManipulationCollector implements ManipulationListener {
		private final List<AtomicManipulation> manipulations = newList();

		public Manipulation toManipulation() {
			return asManipulation(manipulations);
		}

		@Override
		public void noticeManipulation(Manipulation manipulation) {
			if (manipulation.manipulationType() != ManipulationType.COMPOUND)
				manipulations.addAll(manipulation.inline());
			else
				manipulations.add((AtomicManipulation) manipulation);
		}
	}

}

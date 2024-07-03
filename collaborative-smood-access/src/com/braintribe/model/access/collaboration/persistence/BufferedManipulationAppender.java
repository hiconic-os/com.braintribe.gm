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
package com.braintribe.model.access.collaboration.persistence;

import static com.braintribe.model.generic.manipulation.util.ManipulationBuilder.compound;
import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.newList;

import java.util.List;

import com.braintribe.exception.Exceptions;
import com.braintribe.model.generic.manipulation.Manipulation;
import com.braintribe.model.generic.manipulation.VoidManipulation;
import com.braintribe.model.generic.reflection.GenericModelException;
import com.braintribe.model.processing.session.api.collaboration.ManipulationPersistenceException;
import com.braintribe.model.processing.session.api.collaboration.PersistenceAppender;
import com.braintribe.model.processing.session.api.managed.ManipulationMode;

/**
 * @author peter.gazdik
 */
public class BufferedManipulationAppender {

	public static final int LIMIT = 100;

	private final PersistenceAppender delegate;
	private final ManipulationMode mode;

	private final List<Manipulation> buffer = newList();

	public BufferedManipulationAppender(PersistenceAppender delegate, ManipulationMode mode) {
		this.delegate = delegate;
		this.mode = mode;
	}

	/* package */ void append(Manipulation manipulation) {
		buffer.add(manipulation);
		if (buffer.size() >= LIMIT)
			flush(false);
	}

	/* package */ void flush() {
		flush(true);
	}

	private void flush(boolean endTransaction) {
		try {
			tryFlush();
			if (endTransaction)
				delegate.append(VoidManipulation.T.create(), null);

		} catch (ManipulationPersistenceException e) {
			Exceptions.uncheckedAndContextualize(e, "Error while ", GenericModelException::new);
		}
	}

	private void tryFlush() throws ManipulationPersistenceException {
		switch (buffer.size()) {
			case 0:
				return;
			case 1:
				delegate.append(first(buffer), mode);
				return;
			default:
				delegate.append(compound(buffer), mode);
		}

		buffer.clear();
	}

}

// ============================================================================
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
package dev.hiconic.servlet.webrpc.server;

import com.braintribe.exception.HasLogPreferences;
import com.braintribe.exception.LogPreferences;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.ReasonException;
import com.braintribe.logging.Logger.LogLevel;

/**
 * Signals that a remote request, evaluated for a client without reasoning support, resolved to an unsatisfied {@link Reason}.
 * <p>
 * It is logged as a single WARN line, with the full stack trace only on DEBUG.
 */
public class UnsatisfiedRequestException extends ReasonException implements HasLogPreferences {

	private static final long serialVersionUID = 1L;

	private static final LogPreferences LOG_PREFERENCES = new LogPreferences(LogLevel.WARN, false, LogLevel.DEBUG);

	public UnsatisfiedRequestException(Reason reason) {
		super(reason);
	}

	@Override
	public LogPreferences getLogPreferences() {
		return LOG_PREFERENCES;
	}
}

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
package com.braintribe.model.processing.webrpc.server.multipart;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import com.braintribe.model.generic.session.InputStreamProvider;
import com.braintribe.utils.stream.DeferredInputStream;

public class DeferredInputStreamSupplier implements Supplier<InputStream>, InputStreamProvider {

	private Object lock = new Object();
	private volatile boolean concluded = false;
	private Supplier<InputStream> initialInputStreamSupplier;
	private boolean repeatable;
	private Supplier<InputStream> reopenableInputStreamSupplier;
	private Throwable deferralFailure;
	private Set<DeferredInputStream> inputStreams = new HashSet<>();

	public DeferredInputStreamSupplier() {
	}

	public InputStream reopen() {

		if (reopenableInputStreamSupplier != null) {
			return reopenableInputStreamSupplier.get();
		}

		if (deferralFailure != null) {
			if (deferralFailure instanceof RuntimeException) {
				throw (RuntimeException) deferralFailure;
			}
			if (deferralFailure instanceof Error) {
				throw (Error) deferralFailure;
			}
			throw new IllegalStateException("Source backup failed: " + deferralFailure, deferralFailure);
		}

		throw new IllegalStateException(this + " is concluded but has no input stream supplier nor failure information");

	}

	@Override
	public InputStream openInputStream() {
		return get();
	}

	@Override
	public InputStream get() {

		if (!concluded) {
			synchronized (lock) {
				if (!concluded) {
					DeferredInputStream inputStream = new DeferredInputStream();
					if (this.initialInputStreamSupplier != null) {
						inputStream.setDelegate(this.initialInputStreamSupplier, this.repeatable, true);
					}
					inputStreams.add(inputStream);
					return inputStream;
				}
			}
		}

		return reopen();

	}

	public void bindDelegate(Supplier<InputStream> delegateSupplier, boolean repeatable) {

		Objects.requireNonNull(delegateSupplier, "delegateSupplier must not be null");

		synchronized (lock) {

			if (initialInputStreamSupplier != null) {
				throw new IllegalStateException(this + " was already initialized");
			}

			this.initialInputStreamSupplier = delegateSupplier;
			this.repeatable = repeatable;

			for (DeferredInputStream deferredInputStream : inputStreams) {
				deferredInputStream.setDelegate(initialInputStreamSupplier, repeatable, true);
			}

		}

	}

	public void markAsConcluded(Supplier<InputStream> inputStreamSupplier, long bytes) {

		Objects.requireNonNull(inputStreamSupplier, "inputStreamSupplier must not be null");

		synchronized (lock) {

			if (this.concluded) {
				throw new IllegalStateException(this + " was already marked as concluded");
			}

			this.reopenableInputStreamSupplier = inputStreamSupplier;

			this.concluded = true; // No more DeferredInputStreams are provided beyond this point

		}

		// Previously provided DeferredInputStreams are marked as concluded, no need for lock here.
		for (DeferredInputStream deferredInputStream : inputStreams) {
			deferredInputStream.markDelegateAsComplete(bytes);
		}

	}

	public void markAsFailed(Throwable backupFailure) {

		synchronized (lock) {

			if (this.concluded) {
				throw new IllegalStateException(this + " was already marked as concluded");
			}

			this.deferralFailure = Objects.requireNonNull(backupFailure, "backupFailure must not be null");

			this.concluded = true; // No more DeferredInputStreams are provided beyond this point

		}

		// Previously provided DeferredInputStreams are marked as concluded/failed, no need for lock here.
		for (DeferredInputStream deferredInputStream : inputStreams) {
			deferredInputStream.markDelegateAsInvalid(this.deferralFailure);
		}

	}

}

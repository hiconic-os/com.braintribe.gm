package com.braintribe.gm.model.reason;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReasonAggregator<R extends Reason> implements Consumer<Reason>, Supplier<R> {

	private Reason reason;
	private boolean wrapped;
	private final Supplier<? extends R> wrapperSupplier;
	private final boolean forceWrap;

	ReasonAggregator(Supplier<? extends R> wrapperSupplier, boolean forceWrap) {
		this.wrapperSupplier = wrapperSupplier;
		this.forceWrap = forceWrap;
	}

	public void acceptMaybe(Maybe<?> maybe) {
		accept(maybe.whyUnsatisfied());
	}

	@Override
	public void accept(Reason r) {
		if (r == null)
			return;

		if (forceWrap) {
			if (reason == null)
				reason = wrapperSupplier.get();

			reason.getReasons().add(r);

		} else {
			if (reason == null)
				reason = r;
			else {
				if (!wrapped) {
					R wrapper = wrapperSupplier.get();
					wrapper.getReasons().add(reason);
					reason = wrapper;
					wrapped = true;
				}

				reason.getReasons().add(r);
			}
		}
	}

	public void forwardIfReasonable(Consumer<Reason> collector) {
		if (reason != null)
			collector.accept(reason);
	}

	@Override
	public R get() {
		return (R) reason;
	}

	public boolean hasReason() {
		return reason != null;
	}
}

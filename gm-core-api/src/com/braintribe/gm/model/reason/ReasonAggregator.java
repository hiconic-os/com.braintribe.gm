package com.braintribe.gm.model.reason;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReasonAggregator<R extends Reason> implements Consumer<Reason>, Supplier<R> {
	private boolean wrapped;
	private Supplier<? extends R> wrapperSupplier;
	private Reason reason;
	private boolean forceWrap;

	ReasonAggregator(Supplier<? extends R> wrapperSupplier, boolean forceWrap) {
		this.wrapperSupplier = wrapperSupplier;
		this.forceWrap = forceWrap;
	}
	
	@Override
	public void accept(Reason r) {
		if (r == null)
			return;
		
		if (forceWrap) {
			if (reason == null)
				reason = wrapperSupplier.get();
	
			reason.getReasons().add(r);
		}
		else {
			if (reason == null)
				reason = r;
			else {
				if (!wrapped) {
					var wrapper = wrapperSupplier.get();
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
	
	public R get() {
		return (R)reason;
	}
	
	public boolean hasReason() {
		return reason != null;
	}
}

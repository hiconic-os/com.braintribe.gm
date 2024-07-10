package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.gm.model.reason.Reason;

public class MappingError extends RuntimeException {
	private Reason reason;
	
	public MappingError(Reason reason) {
		super(reason.getText());
		this.reason = reason;
	}
	
	public MappingError(Reason reason, MappingError cause) {
		this(reason);
		reason.getReasons().add(cause.getReason());
	}
	
	public Reason getReason() {
		return reason;
	}
}

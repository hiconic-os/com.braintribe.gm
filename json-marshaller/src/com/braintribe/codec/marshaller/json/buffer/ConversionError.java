package com.braintribe.codec.marshaller.json.buffer;

import com.braintribe.gm.model.reason.Reason;

public class ConversionError extends Exception {
	private Reason reason;
	
	public ConversionError(Reason reason) {
		super(reason.getText());
		this.reason = reason;
	}
	
	public ConversionError(Reason reason, ConversionError cause) {
		this(reason);
		reason.getReasons().add(cause.getReason());
	}
	
	public Reason getReason() {
		return reason;
	}
}

package com.braintribe.model.generic.reason;

import java.util.NoSuchElementException;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.ConfigurationError;
import com.braintribe.gm.model.reason.essential.InternalError;
import com.braintribe.gm.model.reason.essential.NotFound;

public class ReasonExceptionLab {
	public static void main(String[] args) {
		
		try {
			foo();
		}
		catch (Exception e) {
			Reason internalError1 = InternalError.from(e, "Error whild doing stuff");
			Reason internalError2 = InternalError.from(e, "Repeated Error whild doing stuff");
			
			Reason notFound = Reasons.build(NotFound.T).text("not found").cause(internalError1).cause(internalError2).toReason();
			
			Reason reason = Reasons.build(ConfigurationError.T).text("your config is wrong").cause(notFound).toReason();
			
			System.out.println(reason.stringify(true));
			
		}
	}
	
	public static void foo() {
		bar();
	}
	
	public static void bar() {
		fix();
	}
	
	public static void fix() {
		fox();
	}
	
	public static void fox() {
		try {
			fax();
		}
		catch (Exception e){
			throw new IllegalArgumentException("Not found", e);
		}
	}
	
	public static void fax() {
		try {
			fex();
		}
		catch (Exception e){
			throw new UnsupportedOperationException("Not supported", e);
		}
	}
	public static void fex() {
		throw new IllegalStateException("You should not do that");
	}
}

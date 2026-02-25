package com.braintribe.gm.config.yaml;

import java.util.function.Function;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.config.PropertyNotFound;

public interface PropertyResolutions {
	String ENV_PREFIX = "env.";
	
	static Function<String, Maybe<String>> reasonifyPropertyResolver(Function<String, String> resolver) {
		return name -> {
			String value = resolver.apply(name);
			
			if (value != null)
				return Maybe.complete(value);
			else
				return PropertyNotFound.create(name).asMaybe();
		};
	}
}

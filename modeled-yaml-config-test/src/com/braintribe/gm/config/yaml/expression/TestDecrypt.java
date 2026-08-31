// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2026
// Licensed under the Apache License, Version 2.0
// ============================================================================
package com.braintribe.gm.config.yaml.expression;

import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.type.StringDescriptor;

@PositionalArguments("cipherText")
public interface TestDecrypt extends StringDescriptor {

	EntityType<TestDecrypt> T = EntityTypes.T(TestDecrypt.class);

	String getCipherText();
	void setCipherText(String cipherText);
}

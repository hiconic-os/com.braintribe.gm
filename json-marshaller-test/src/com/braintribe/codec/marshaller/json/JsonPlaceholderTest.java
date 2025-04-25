package com.braintribe.codec.marshaller.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.PlaceholderSupport;
import com.braintribe.codec.marshaller.json.model.TestEntity;
import com.braintribe.gm.config.ConfigVariableResolver;

public class JsonPlaceholderTest {

	@Test
	public void testSimplePlaceholder() throws FileNotFoundException, IOException {
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		marshaller.setUseBufferingDecoder(true);
		GmDeserializationOptions options = GmDeserializationOptions.deriveDefaults().set(PlaceholderSupport.class, true).build();
		
		File file = new File("res/simple-placeholder.json");
		
		final TestEntity testEntity;
		
		try (InputStream in = new FileInputStream(file)) {
			testEntity = (TestEntity) marshaller.unmarshallReasoned(in, options).get();
		}
		
		Map<String, String> vars = Map.of("DECIMAL_VALUE", "0.5", "INTEGER_VALUE", "23");
		
		ConfigVariableResolver resolver = new ConfigVariableResolver();
		resolver.setVariableResolver(vars::get);
		
		TestEntity entity = resolver.resolvePlaceholders(testEntity).get();
		
		Assertions.assertThat(entity.getDecimalValue()).isEqualTo(new BigDecimal("0.5"));
		Assertions.assertThat(entity.getStringIntegerMap().get("intValue")).isEqualTo(23);
	}
}

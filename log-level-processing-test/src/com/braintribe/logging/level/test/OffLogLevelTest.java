package com.braintribe.logging.level.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.logging.Level;

import org.junit.Test;

import com.braintribe.logging.level.JulLogLevelFramework;
import com.braintribe.logging.level.LogLevelConfiguration;

public class OffLogLevelTest {
	@Test
	public void supportsOffInConfigurationAndJul() {
		assertThat(LogLevelConfiguration.isSupportedLogLevel("OFF")).isTrue();
		assertThat(LogLevelConfiguration.normalizeLogLevel(" off ")).isEqualTo("OFF");
		assertThat(JulLogLevelFramework.toJulLevel("OFF")).isSameAs(Level.OFF);
		assertThat(JulLogLevelFramework.fromJulLevel(Level.OFF)).isEqualTo("OFF");
	}
}

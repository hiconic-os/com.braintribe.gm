package dev.hiconic.template.api;

import java.util.Objects;

public final class TemplateParserOptions {
	public static final TemplateParserOptions STRICT =
			new TemplateParserOptions(ParseRecoveryMode.STRICT, "[[TEMPLATE ERROR: ", "]]");
	public static final TemplateParserOptions SUBSTITUTE =
			new TemplateParserOptions(ParseRecoveryMode.SUBSTITUTE, "[[TEMPLATE ERROR: ", "]]");
	public static final TemplateParserOptions SILENCE =
			new TemplateParserOptions(ParseRecoveryMode.SILENCE, "", "");

	private final ParseRecoveryMode recoveryMode;
	private final String errorPrefix;
	private final String errorSuffix;

	public TemplateParserOptions(ParseRecoveryMode recoveryMode, String errorPrefix, String errorSuffix) {
		this.recoveryMode = Objects.requireNonNull(recoveryMode, "recoveryMode");
		this.errorPrefix = Objects.requireNonNull(errorPrefix, "errorPrefix");
		this.errorSuffix = Objects.requireNonNull(errorSuffix, "errorSuffix");
	}

	public ParseRecoveryMode recoveryMode() {
		return recoveryMode;
	}

	public String errorPrefix() {
		return errorPrefix;
	}

	public String errorSuffix() {
		return errorSuffix;
	}
}

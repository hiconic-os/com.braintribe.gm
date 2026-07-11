package dev.hiconic.template.api;

import dev.hiconic.template.impl.StandardTemplateExperts;
import com.braintribe.utils.lcd.Lazy;

public final class TemplateFactories {
	private static final Lazy<TemplateFactory> BASE = new Lazy<>(() -> TemplateFactory.builder()
			.configure(StandardTemplateExperts::registerBase)
			.build());
	private static final Lazy<TemplateFactory> HTML = new Lazy<>(() -> BASE.get()
			.derive(StandardTemplateExperts::registerHtmlDefaults));
	private static final Lazy<TemplateFactory> XML = new Lazy<>(() -> BASE.get()
			.derive(StandardTemplateExperts::registerXmlDefaults));
	private static final Lazy<TemplateFactory> JSON = new Lazy<>(() -> BASE.get()
			.derive(StandardTemplateExperts::registerJsonDefaults));
	private static final Lazy<TemplateFactory> CSS = new Lazy<>(() -> BASE.get()
			.derive(StandardTemplateExperts::registerCssDefaults));
	private static final Lazy<TemplateFactory> JAVA_SCRIPT = new Lazy<>(() -> BASE.get()
			.derive(StandardTemplateExperts::registerJavaScriptDefaults));
	private static final Lazy<TemplateFactory> URL_COMPONENT = new Lazy<>(() -> BASE.get()
			.derive(StandardTemplateExperts::registerUrlComponentDefaults));

	private TemplateFactories() {
	}

	public static TemplateFactory base() {
		return BASE.get();
	}

	public static TemplateFactory html() {
		return HTML.get();
	}

	public static TemplateFactory xml() {
		return XML.get();
	}

	public static TemplateFactory json() {
		return JSON.get();
	}

	public static TemplateFactory css() {
		return CSS.get();
	}

	public static TemplateFactory javaScript() {
		return JAVA_SCRIPT.get();
	}

	public static TemplateFactory urlComponent() {
		return URL_COMPONENT.get();
	}
}

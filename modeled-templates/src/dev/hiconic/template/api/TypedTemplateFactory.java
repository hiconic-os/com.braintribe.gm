package dev.hiconic.template.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.impl.ConfigurableTemplateExpertRegistry;
import dev.hiconic.template.impl.ModeledTemplate;
import dev.hiconic.template.impl.parser.StandardTemplateApiResolver;
import dev.hiconic.template.impl.parser.StandardTemplateParser;
import dev.hiconic.template.model.core.TemplateNode;

public final class TypedTemplateFactory<I> {
	private final TemplateFactory factory;
	private final GenericModelType rootType;

	TypedTemplateFactory(TemplateFactory factory, GenericModelType rootType) {
		this.factory = Objects.requireNonNull(factory, "factory");
		this.rootType = Objects.requireNonNull(rootType, "rootType");
	}

	public GenericModelType rootType() {
		return rootType;
	}

	public Maybe<Template<I>> parse(String source) {
		ConfigurableTemplateExpertRegistry registry = factory.registry();
		StandardTemplateApiResolver resolver = new StandardTemplateApiResolver(
				registry, rootType, factory.rootVariable(), factory.inputCmdResolver(), factory.expertCmdResolver());
		StandardTemplateParser parser = new StandardTemplateParser(resolver, resolver, factory.options());
		Maybe<TemplateNode> parsed = parser.parse(source);
		if (parsed.isUnsatisfied())
			return Maybe.empty(parsed.whyUnsatisfied());

		Template<I> template = new ModeledTemplate<>(
				parsed.value(), rootType, factory.rootVariable(), registry, factory.allowNoEscape(), factory.defaults());
		return parsed.isIncomplete()
				? Maybe.incomplete(template, parsed.whyUnsatisfied())
				: Maybe.complete(template);
	}

	public Maybe<Template<I>> parse(InputStream input, Charset charset) throws IOException {
		return parse(new String(input.readAllBytes(), charset));
	}

	public Maybe<Template<I>> parse(Reader reader) throws IOException {
		StringBuilder source = new StringBuilder();
		char[] buffer = new char[8192];
		for (int read = reader.read(buffer); read >= 0; read = reader.read(buffer))
			source.append(buffer, 0, read);
		return parse(source.toString());
	}

	public Maybe<Template<I>> parse(InputStream input) throws IOException {
		return parse(input, StandardCharsets.UTF_8);
	}

	public Maybe<Template<I>> parse(Path file, Charset charset) throws IOException {
		return parse(Files.readString(file, charset));
	}

	public Maybe<Template<I>> parse(Path file) throws IOException {
		return parse(file, StandardCharsets.UTF_8);
	}

	public Maybe<Template<I>> parse(File file, Charset charset) throws IOException {
		return parse(file.toPath(), charset);
	}

	public Maybe<Template<I>> parse(File file) throws IOException {
		return parse(file.toPath());
	}
}

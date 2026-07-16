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
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.CustomType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Model;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;

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
		CmdResolver inputCmdResolver = effectiveInputCmdResolver();
		CmdResolver expertCmdResolver = effectiveExpertCmdResolver();
		StandardTemplateApiResolver resolver = new StandardTemplateApiResolver(
				registry, rootType, factory.rootVariable(), inputCmdResolver, expertCmdResolver,
				factory.templates());
		StandardTemplateParser parser = new StandardTemplateParser(resolver, resolver, factory.options());
		Maybe<TemplateNode> parsed = parser.parse(source);
		if (parsed.isUnsatisfied())
			return Maybe.empty(parsed.whyUnsatisfied());

		Template<I> template = new ModeledTemplate<>(
				parsed.value(), rootType, factory.rootVariable(), registry, factory.allowNoEscape(), factory.defaults(),
				factory.templates());
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

	private CmdResolver effectiveInputCmdResolver() {
		CmdResolver configured = factory.inputCmdResolver();
		if (configured != null)
			return configured;

		GmMetaModel metaModel = effectiveInputMetaModel();
		if (metaModel == null)
			return null;
		return CmdResolverImpl.create(new BasicModelOracle(metaModel)).done();
	}

	private CmdResolver effectiveExpertCmdResolver() {
		CmdResolver configured = factory.expertCmdResolver();
		if (configured != null)
			return configured;

		Model model = TemplateNode.T.getModel();
		if (model == null)
			return null;
		return CmdResolverImpl.create(new BasicModelOracle(model.getMetaModel())).done();
	}

	private GmMetaModel effectiveInputMetaModel() {
		Model model = findModel(rootType);
		return model == null ? null : model.getMetaModel();
	}

	private static Model findModel(GenericModelType type) {
		if (type instanceof CustomType customType)
			return customType.getModel();
		if (type instanceof MapType mapType) {
			Model keyModel = findModel(mapType.getKeyType());
			return keyModel != null ? keyModel : findModel(mapType.getValueType());
		}
		if (type instanceof CollectionType collectionType)
			return findModel(collectionType.getCollectionElementType());
		return null;
	}
}

package dev.hiconic.template.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.braintribe.model.generic.reflection.GenericModelType;

import dev.hiconic.template.model.core.TemplateNode;

public interface Template<I> {
	GenericModelType rootType();
	String rootVariable();

	TemplateNode rootNode();
	void rootNode(TemplateNode rootNode);

	void evaluate(I input, OutputStream output, Charset charset) throws IOException;

	default void evaluate(I input, OutputStream output) throws IOException {
		evaluate(input, output, StandardCharsets.UTF_8);
	}

	default String evaluateToString(I input) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			evaluate(input, output, StandardCharsets.UTF_8);
			return output.toString(StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Unexpected in-memory template evaluation failure", e);
		}
	}

	default void evaluateToFile(I input, Path file, Charset charset) throws IOException {
		try (OutputStream output = Files.newOutputStream(file)) {
			evaluate(input, output, charset);
		}
	}

	default void evaluateToFile(I input, Path file) throws IOException {
		evaluateToFile(input, file, StandardCharsets.UTF_8);
	}
}

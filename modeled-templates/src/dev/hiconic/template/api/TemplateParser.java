package dev.hiconic.template.api;

import java.io.IOException;
import java.io.Reader;

import com.braintribe.gm.model.reason.Maybe;

import dev.hiconic.template.model.core.TemplateNode;

public interface TemplateParser {
	Maybe<TemplateNode> parse(String source);

	default Maybe<TemplateNode> parse(Reader reader) throws IOException {
		StringBuilder source = new StringBuilder();
		char[] buffer = new char[8192];
		for (int read = reader.read(buffer); read >= 0; read = reader.read(buffer))
			source.append(buffer, 0, read);
		return parse(source.toString());
	}
}

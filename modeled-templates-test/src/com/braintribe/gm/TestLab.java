package com.braintribe.gm;
import java.io.File;
import java.io.IOException;

import com.braintribe.model.service.api.result.Neutral;

import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateFactories;

public class TestLab {
	public static void main(String[] args) {
		try {
			Template<Neutral> template = TemplateFactories.html().withEnumRoot(Neutral.class).parse(new File("test.html")).get();
			template.evaluate(Neutral.NEUTRAL, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

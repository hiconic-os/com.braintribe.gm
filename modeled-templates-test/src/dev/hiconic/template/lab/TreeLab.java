package dev.hiconic.template.lab;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.ReasonException;

import dev.hiconic.template.api.Template;
import dev.hiconic.template.api.TemplateFactories;
import dev.hiconic.template.test.model.TestPerson;

public class TreeLab {
	public static void main(String[] args) throws ReasonException, IOException {
		TestPerson peter = TestPerson.T.create();
		TestPerson tina = TestPerson.T.create();
		TestPerson dirk = TestPerson.T.create();
		TestPerson rene = TestPerson.T.create();
		TestPerson sabsi = TestPerson.T.create();
		TestPerson elli = TestPerson.T.create();
		
		dirk.setName("Dirk");
		peter.setName("Peter");
		tina.setName("Tina");
		elli.setName("Elli");
		rene.setName("Rene");
		sabsi.setName("Sabsi");
		
		dirk.getFriends().addAll(List.of(peter, tina, rene));
		tina.getFriends().add(sabsi);
		rene.getFriends().add(elli);
		
		Maybe<Template<TestPerson>> maybe = TemplateFactories.html().withRoot(TestPerson.T).parse(new File("res/tree.html.mt"));
		
		if (maybe.isUnsatisfied()) {
			System.out.println(maybe.whyUnsatisfied().stringify());
			return;
		}
		
		Template<TestPerson> template = maybe.get();
		
		template.evaluate(dirk, System.out);
		
	}
}

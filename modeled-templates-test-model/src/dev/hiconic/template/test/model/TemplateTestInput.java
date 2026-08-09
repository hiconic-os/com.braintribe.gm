package dev.hiconic.template.test.model;

import java.util.Date;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.PropertyLiteral;
import com.braintribe.model.resource.Resource;

public interface TemplateTestInput extends GenericEntity {
	EntityType<TemplateTestInput> T = EntityTypes.T(TemplateTestInput.class);

	PropertyLiteral title = PropertyLiteral.of(T, "title");
	PropertyLiteral birthday = PropertyLiteral.of(T, "birthday");
	PropertyLiteral persons = PropertyLiteral.of(T, "persons");
	PropertyLiteral logo = PropertyLiteral.of(T, "logo");
	PropertyLiteral attachment = PropertyLiteral.of(T, "attachment");
	PropertyLiteral documents = PropertyLiteral.of(T, "documents");

	String getTitle();
	void setTitle(String title);

	Date getBirthday();
	void setBirthday(Date birthday);

	List<TestPerson> getPersons();
	void setPersons(List<TestPerson> persons);

	/** An image resource, for exercising the {@code Resource -> ImageOutput} document conversion. */
	Resource getLogo();
	void setLogo(Resource logo);

	/** A single document resource, for the {@code Resource -> EmbeddedDocumentOutput} attachment conversion. */
	Resource getAttachment();
	void setAttachment(Resource attachment);

	/** A collection of document resources, for embedding many attachments via {@code for-each}. */
	List<Resource> getDocuments();
	void setDocuments(List<Resource> documents);
}

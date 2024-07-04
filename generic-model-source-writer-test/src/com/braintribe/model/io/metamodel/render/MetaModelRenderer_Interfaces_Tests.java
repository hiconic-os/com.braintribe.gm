// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.io.metamodel.render;

import org.junit.Test;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.annotation.GlobalId;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.TypeRestriction;
import com.braintribe.model.generic.annotation.meta.Alias;
import com.braintribe.model.generic.annotation.meta.CompoundUnique;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Max;
import com.braintribe.model.generic.annotation.meta.Min;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.annotation.meta.PositionalArguments;
import com.braintribe.model.generic.base.EnumBase;
import com.braintribe.model.generic.reflection.EnumType;
import com.braintribe.model.generic.reflection.EnumTypes;
import com.braintribe.model.io.metamodel.testbase.MetaModelBuilder;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmProperty;

public class MetaModelRenderer_Interfaces_Tests extends MetaModelRendererTestBase {

	@Test
	public void testGeneratesCodeForSimpleInterface() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.PERSON_TYPE_SIGNATURE);
		renderEntityType(gmEntityType);
		assertContainsSubstring("package " + MetaModelBuilder.COMMON_PACKAGE);
		assertHasNoAnnotation(GlobalId.class);
		assertHasProperty("Name", "String");
		assertHasProperty("Age", "Integer");
		assertContainsSubstring("EntityType<Person> T = EntityTypes.T(Person.class);");

		assertNotContains("extends");
	}

	@Test
	public void testUsesFullyQualifiedNameIfEntityNameConflictsWithImport() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.ENTITY_TYPE_TYPE_SIGNATURE);
		renderEntityType(gmEntityType);
		assertContainsSubstring("package " + MetaModelBuilder.COMMON_PACKAGE);
		assertContainsSubstring("public interface EntityType extends EntityTypes");
		assertContainsSubstring(
				"com.braintribe.model.generic.reflection.EntityType<EntityType> T = com.braintribe.model.generic.reflection.EntityTypes.T(EntityType.class);");
		assertNotContains("import");
	}

	@Test
	public void testGeneratesCodeForSimpleInterface_CustomGlobalId() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.PERSON_TYPE_SIGNATURE);

		gmEntityType.setGlobalId("entityGlobalid");
		gmEntityType.getProperties().forEach(p -> p.setGlobalId("property:" + p.getName()));

		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(GlobalId.class);
		assertHasProperty("Name", "String");
		assertHasProperty("Age", "Integer");
		assertContainsSubstring("package " + MetaModelBuilder.COMMON_PACKAGE);
		assertContainsSubstring("EntityType<Person> T = EntityTypes.T(Person.class);");

		assertContainsSubstring("@GlobalId(\"" + gmEntityType.getGlobalId() + "\")");
		for (GmProperty p : gmEntityType.getProperties())
			assertContainsSubstring("@GlobalId(\"" + p.getGlobalId() + "\")");

		assertNotContains("extends");
	}

	@Test
	public void testGeneratesCodeForSimpleEnum() throws Exception {
		GmEnumType gmEnumType = getEnumBySignature(MetaModelBuilder.ENUM_TYPE_SIGNATURE);
		renderEnumType(gmEnumType);
		assertHasNoAnnotation(GlobalId.class);

		assertHasImport(EnumBase.class);
		assertHasImport(EnumType.class);
		assertHasImport(EnumTypes.class);

		assertContainsSubstrings("RED,", "GREEN,", "BLUE,");
		assertContainsSubstrings("BLUE,\n\t;");
		assertContainsSubstring("package " + MetaModelBuilder.COMMON_PACKAGE);

		assertContainsSubstring("public static final EnumType<TheEnum> T = EnumTypes.T(TheEnum.class);");
		assertContainsSubstring("public EnumType<TheEnum> type() {");
		assertContainsSubstring("return T;");
	}

	@Test
	public void testGeneratesCodeForSimpleEnum_CustomGlobalId() throws Exception {
		GmEnumType gmEnumType = getEnumBySignature(MetaModelBuilder.ENUM_TYPE_SIGNATURE);

		gmEnumType.setGlobalId("enumGlobalid");
		gmEnumType.getConstants().forEach(c -> c.setGlobalId("constant:" + c.getName()));

		renderEnumType(gmEnumType);
		assertHasImportedAnnotation(GlobalId.class);
		assertContainsSubstrings("RED,", "GREEN,", "BLUE,");
		assertContainsSubstring("package " + MetaModelBuilder.COMMON_PACKAGE);

		assertContainsSubstring("@GlobalId(\"" + gmEnumType.getGlobalId() + "\")");
		for (GmEnumConstant c : gmEnumType.getConstants())
			assertContainsSubstring("@GlobalId(\"" + c.getGlobalId() + "\")");

		assertContainsSubstring("public static final EnumType<TheEnum> T = EnumTypes.T(TheEnum.class);");
		assertContainsSubstring("public EnumType<TheEnum> type() {");
		assertContainsSubstring("return T;");
	}

	@Test
	public void testGeneratesCodeForInterfaceWithSuperInterface() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.SPECIAL_PERSON_TYPE_SIGNATURE);
		renderEntityType(gmEntityType);
		assertHasProperty("Title", "String");
		assertHasProperty("Slave", "Person");
		assertHasProperty("LongAge", "Long");
		// Properties inherited from SuperInterface are not rendered
		assertDoesntHaveProperty("Name", "String");
		assertDoesntHaveProperty("Age", "Integer");
		assertContainsSubstring(" extends Person");

	}

	@Test
	public void testGeneratesCodeForInterfaceWithSuperInterfaceInOtherPackage() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.INTERFACE_FROM_OTHER_PACKAGE_SIGNATURE);
		renderEntityType(gmEntityType);
		assertContainsSubstring(".subpackage");
		assertContainsSubstring(" extends Person");
	}

	@Test
	public void testAbstractInterfaceHasAnnotation() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.ABSTRACT_INTERFACE);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Abstract.class);
	}

	@Test
	public void testWorksForInterfaceWithoutPackage() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.INTERFACE_WITHOUT_PACKAGE);
		renderEntityType(gmEntityType);
		assertNotContains("package");
	}

	@Test
	public void testWorksForEnumWithoutPackage() throws Exception {
		GmEnumType gmEnumType = getEnumBySignature(MetaModelBuilder.ENUM_WITHOUT_PACKAGE);
		renderEnumType(gmEnumType);
		assertNotContains("package");
	}

	@Test
	public void testWritesPropertyOfBaseTypeCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_BASE_TYPE);
		renderEntityType(gmEntityType);
		assertHasProperty("Base", Object.class.getSimpleName());
	}

	@Test
	public void testWritesInitializedPropertyCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_INITIALIZER);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Initializer.class, "'joe'");
		assertHasImportedAnnotation(Initializer.class, "GREEN");
		assertHasImportedAnnotation(Initializer.class, "enum(" + MetaModelBuilder.ENUM_TYPE_SIGNATURE + ",GREEN)");
	}

	@Test
	public void testWritesEvaluatesToCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_EVALUATES_TO);
		renderEntityType(gmEntityType);
		assertContainsSubstring("EvalContext<String> eval(Evaluator<ServiceRequest> evaluator);");
	}

	@Test
	public void testWritesJavaKeywordPropertiesCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_KEYWORD_PROPS);
		renderEntityType(gmEntityType);
		assertHasEscapedProperty("For", "String", "for_", "for_");
		assertHasEscapedProperty("For_", "String", "for__", "for_");
		assertHasEscapedProperty("Foobar_", "String", "foobar__", "foobar_");
	}

	@Test
	public void writesTypeRestrictionCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_TYPE_RESTRICTION);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(TypeRestriction.class);
		assertContainsSubstring("@TypeRestriction(value={" + MetaModelBuilder.PERSON_TYPE_SIGNATURE + ".class," + MetaModelBuilder.WITH_INITIALIZER
				+ ".class},key={},allowVd=true,allowKeyVd=false)");
	}

	@Test
	public void testWritesMinMaxCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_MIN_MAX_PROPERTY);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Min.class);
		assertHasImportedAnnotation(Max.class);
		assertContainsSubstring("@Max(exclusive=false, value=\"100\")");
		assertContainsSubstring("@Min(exclusive=false, value=\"0\")");
	}

	@Test
	public void testWritesSelectiveInformationCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_SELECTIVE_INFORMATION);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(SelectiveInformation.class);
		assertContainsSubstring("@SelectiveInformation(value=\"Selective INFO\")");
	}

	@Test
	public void testWritesCompoundUniqueCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_COMPOUND_UNIQUE);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(CompoundUnique.class);
		assertContainsSubstring("@CompoundUnique(value={\"prop1\", \"prop2\"})");
	}

	@Test
	public void testWritesCompoundUniquesCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_COMPOUND_UNIQUES);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(CompoundUnique.class);
		assertContainsSubstring("@CompoundUnique(globalId=\"gid1\", value={\"prop1\", \"prop2\"})");
		assertContainsSubstring("@CompoundUnique(globalId=\"gid2\", value={\"propA\", \"propB\"})");
	}

	@Test
	public void testWritesAliasCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_ALIAS);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Alias.class);
		assertContainsSubstring("@Alias(value=\"alias\")");
	}

	@Test
	public void testWritesAliasesCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_ALIASES);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Alias.class);
		assertContainsSubstring("@Alias(globalId=\"alias2\", value=\"ALIAS\")");
	}

	@Test
	public void testWritesAliasesCorrectly_NaturalGid() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_NATURAL_GID_ALIASES);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Alias.class);
		assertContainsSubstring("@Alias(value=\"ALIAS\")");
	}

	@Test
	public void testWritesPositionalArgumentsCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_POSITIONAL_ARGUMENTS);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(PositionalArguments.class);
		assertContainsSubstring("@PositionalArguments(value={\"a1\", \"a2\"})");
	}

	@Test
	public void testWritesNameCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_NAME);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Name.class);
		// NOTE globalId is omitted as it is the natural one
		assertContainsSubstring("@Name(locale=\"default\", value=\"default name\")");
	}

	@Test
	public void testWritesNamesCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_NAMES);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Name.class);
		assertContainsSubstring("@Name(globalId=\"gid1\", locale=\"de\", value=\"Der Name\")");
		assertContainsSubstring("@Name(globalId=\"gid1\", locale=\"default\", value=\"default name\")");
	}

	@Test
	public void testWritesDescriptionsCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.WITH_DESCRIPTIONS);
		renderEntityType(gmEntityType);
		assertHasImportedAnnotation(Description.class);
		assertContainsSubstring("@Description(globalId=\"gid1\", locale=\"de\", value=\"Unfug\")");
		assertContainsSubstring("@Description(globalId=\"gid1\", locale=\"default\", value=\"default description\")");
	}

	@Test
	public void testWritesEnumDescriptionsCorrectly() throws Exception {
		GmEnumType gmEnumType = getEnumBySignature(MetaModelBuilder.ENUM_WITH_MD_ANNOTATIONS);
		renderEnumType(gmEnumType);

		// On Enum itself
		assertHasImportedAnnotation(Description.class);
		assertContainsSubstring("@Description(globalId=\"gid1\", locale=\"de\", value=\"Unfug\")");
		assertContainsSubstring("@Description(globalId=\"gid1\", locale=\"default\", value=\"default description\")");

		// On Constant_!
		assertHasImportedAnnotation(Min.class);
		assertHasImportedAnnotation(Max.class);
		assertContainsSubstring("@Max(exclusive=false, value=\"100\")");
		assertContainsSubstring("@Min(exclusive=false, value=\"0\")");
	}

	@Test
	public void testWritesDeprecatedCorrectly() throws Exception {
		GmEntityType gmEntityType = getTypeBySignature(MetaModelBuilder.DEPRECATED_ENTITY);
		renderEntityType(gmEntityType);
		assertContainsSubstring("@Deprecated");
		assertNotContains("@Deprecated("); // no attributes like globalId
		assertNotContains("java.lang.Deprecated"); // no import
	}

}

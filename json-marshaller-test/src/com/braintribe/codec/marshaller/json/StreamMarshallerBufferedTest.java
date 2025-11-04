// ============================================================================
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
package com.braintribe.codec.marshaller.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.braintribe.codec.marshaller.api.CmdResolverOption;
import com.braintribe.codec.marshaller.api.DecodingLenience;
import com.braintribe.codec.marshaller.api.EntityRecurrenceDepth;
import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.IdentityManagementMode;
import com.braintribe.codec.marshaller.api.IdentityManagementModeOption;
import com.braintribe.codec.marshaller.api.TypeExplicitness;
import com.braintribe.codec.marshaller.api.TypeExplicitnessOption;
import com.braintribe.gm._TestModel_;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.NotFound;
import com.braintribe.gm.model.reason.essential.ParseError;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.reflection.ListType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.cmd.CmdResolver;
import com.braintribe.model.processing.meta.cmd.CmdResolverImpl;
import com.braintribe.model.processing.meta.oracle.BasicModelOracle;
import com.braintribe.testing.model.test.technical.features.EnumEntity;
import com.braintribe.testing.model.test.technical.features.poly.PolyA;
import com.braintribe.testing.model.test.technical.features.poly.PolyB;
import com.braintribe.testing.model.test.technical.features.poly.PolyBase;
import com.braintribe.testing.model.test.technical.features.poly.PolyC;
import com.braintribe.utils.IOTools;

public class StreamMarshallerBufferedTest extends StreamMarshallerTest {

	@Override
	protected JsonStreamMarshaller newJsonMarshaller() {
		JsonStreamMarshaller marshaller = super.newJsonMarshaller();
		marshaller.setUseBufferingDecoder(true);
		return marshaller;
	}

	@Test
	public void successfulPolymorphicResolutionByIndividualProperty() {
		ListType listType = GMF.getTypeReflection().getListType(PolyBase.T);
		List<PolyBase> polies = new ArrayList<>();
		String stringValue = "foobar";

		PolyA polyA = PolyA.T.create();
		polyA.setA(stringValue);
		PolyB polyB = PolyB.T.create();
		polyB.setB(true);

		polies.add(polyA);
		polies.add(polyB);

		JsonStreamMarshaller marshaller = newJsonMarshaller();

		GmSerializationOptions so = GmSerializationOptions.deriveDefaults() //
				.setInferredRootType(listType) //
				.set(TypeExplicitnessOption.class, TypeExplicitness.never) //
				.set(IdentityManagementModeOption.class, IdentityManagementMode.off) //
				.set(EntityRecurrenceDepth.class, 1) //
				.build();

		String s = marshaller.encode(polies, so);

		GmMetaModel model = GMF.getTypeReflection().getModel(_TestModel_.name).getMetaModel();
		CmdResolver cmdResolver = CmdResolverImpl.create(new BasicModelOracle(model)).done();

		GmDeserializationOptions dso = GmDeserializationOptions.deriveDefaults() //
				.setInferredRootType(listType) //
				.set(IdentityManagementModeOption.class, IdentityManagementMode.off) //
				.set(CmdResolverOption.class, cmdResolver) //
				.build();

		List<PolyBase> polies2 = (List<PolyBase>) marshaller.decode(s, dso);

		Assertions.assertThat(polies.size()).isEqualTo(2);

		PolyBase p1 = polies2.get(0);
		PolyBase p2 = polies2.get(1);

		Assertions.assertThat(p1).isInstanceOf(PolyA.class);
		Assertions.assertThat(p2).isInstanceOf(PolyB.class);

		PolyA a = (PolyA) p1;
		PolyB b = (PolyB) p2;

		Assertions.assertThat(a.getA()).isEqualTo(stringValue);
		Assertions.assertThat(b.getB()).isEqualTo(true);

	}

	@Test
	public void unsuccessfulPolymorphicResolutionByIndividualProperty() {
		ListType listType = GMF.getTypeReflection().getListType(PolyBase.T);
		List<PolyBase> polies = new ArrayList<>();
		String stringValue = "foobar";

		PolyA polyA = PolyA.T.create();
		polyA.setA(stringValue);
		PolyC polyC = PolyC.T.create();
		polyC.setC(5);

		polies.add(polyA);
		polies.add(polyC);

		JsonStreamMarshaller marshaller = newJsonMarshaller();

		GmSerializationOptions so = GmSerializationOptions.deriveDefaults() //
				.setInferredRootType(listType) //
				.set(TypeExplicitnessOption.class, TypeExplicitness.never) //
				.set(IdentityManagementModeOption.class, IdentityManagementMode.off) //
				.set(EntityRecurrenceDepth.class, 1) //
				.build();

		String s = marshaller.encode(polies, so);

		GmMetaModel model = GMF.getTypeReflection().getModel(_TestModel_.name).getMetaModel();
		CmdResolver cmdResolver = CmdResolverImpl.create(new BasicModelOracle(model)).done();

		GmDeserializationOptions dso = GmDeserializationOptions.deriveDefaults() //
				.setInferredRootType(listType) //
				.set(IdentityManagementModeOption.class, IdentityManagementMode.off) //
				.set(CmdResolverOption.class, cmdResolver) //
				.build();

		Maybe<?> maybe = marshaller.decodeReasoned(s, dso);

		Assertions.assertThat(maybe.isUnsatisfied()).isTrue();

		Reason reason = getUniqueRootCause(maybe.whyUnsatisfied());
		Assertions.assertThat(reason).isInstanceOf(NotFound.class);
		Assertions.assertThat(reason.getText())
				.startsWith("Cannot resolve polymorphic ambiguity for abstract entity type [" + PolyBase.T.getTypeSignature() + "]");
	}

	@Test
	public void testEnumLenience() throws IOException {
		String jsonInput = IOTools.slurp(new File("res/lenient-enum.json"), "UTF-8");

		JsonStreamMarshaller marshaller = newJsonMarshaller();

		GmDeserializationOptions options = GmDeserializationOptions.deriveDefaults().setInferredRootType(EnumEntity.T).build();

		Maybe<?> maybeFailed = marshaller.decodeReasoned(jsonInput, options);

		Assertions.assertThat(maybeFailed.isUnsatisfiedBy(ParseError.T)).isTrue();

		DecodingLenience decodingLenience = new DecodingLenience();
		decodingLenience.setEnumConstantLenient(true);
		GmDeserializationOptions lenientOptions = GmDeserializationOptions.deriveDefaults() //
				.setInferredRootType(EnumEntity.T)//
				.setDecodingLenience(decodingLenience) //
				.build();

		Maybe<?> maybeSuccess = marshaller.decodeReasoned(jsonInput, lenientOptions);

		Assertions.assertThat(maybeSuccess.isSatisfied()).isTrue();

		// with property lenience enabled decoding must work
		EnumEntity entity = (EnumEntity) maybeSuccess.get();
		assertThat(entity.getSimpleEnum()).isNull();
	}
}

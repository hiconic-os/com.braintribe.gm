package com.braintribe.gm.graphfetching;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.braintribe.gm._GraphFetchingTestModel_;
import com.braintribe.gm.graphfetching.api.Fetching;
import com.braintribe.gm.graphfetching.api.node.EntityGraphNode;
import com.braintribe.gm.graphfetching.test.model.Company;
import com.braintribe.gm.graphfetching.test.model.Person;
import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.path.api.IListItemModelPathElement;
import com.braintribe.model.generic.path.api.IMapKeyModelPathElement;
import com.braintribe.model.generic.path.api.IMapValueModelPathElement;
import com.braintribe.model.generic.path.api.IModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyModelPathElement;
import com.braintribe.model.generic.path.api.IPropertyRelatedModelPathElement;
import com.braintribe.model.generic.path.api.ISetItemModelPathElement;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EssentialCollectionTypes;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.SetType;
import com.braintribe.model.processing.core.commons.comparison.AssemblyComparison;
import com.braintribe.model.processing.core.commons.comparison.AssemblyComparisonResult;
import com.braintribe.model.processing.query.building.EntityQueries;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;
import com.braintribe.model.query.EntityQuery;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;
import com.braintribe.testing.tools.gm.GmTestTools;

public class GraphFetchingTest implements GraphFetchingTestConstants {

	private static SmoodAccess access = GmTestTools.newSmoodAccessMemoryOnly(ACCESS_ID_TEST, GMF.getTypeReflection().getModel(_GraphFetchingTestModel_.name).getMetaModel()); 

	private 	static TestDataSeeder seeder = new TestDataSeeder();
	
	@BeforeClass
	public static void init() {
		PersistenceGmSession session = GmTestTools.newSession(access);
		new TestDataSeeder(session);
		session.commit();
	}
	
	@Test
	public void testIdmFetching() {
		PersistenceGmSession session = GmTestTools.newSession(access);
		List<Company> companies = seeder.getIdmCompanies();

		Company companyPrototype = Fetching.graphPrototype(Company.T);
		
		companyPrototype.getAddress().getCity();
		companyPrototype.getLawyer().getAddress();
		companyPrototype.getOwners().iterator().next().getBestFriend();
		
		EntityGraphNode rootNode = Fetching.rootNode(companyPrototype);
		
		List<Company> actualCompanies = Fetching.fetchDetached(session, rootNode, companies);

		Person p1 = actualCompanies.get(0).getLawyer();
		Person p2 = actualCompanies.get(1).getOwners().iterator().next();
		
		Assertions.assertThat(p1).isSameAs(p2);
		Assertions.assertThat(p1.getAddress()).isNotNull();
		Assertions.assertThat(p1.getBestFriend()).isNotNull();
		
		Person p3 = actualCompanies.get(1).getLawyer();
		Person p4 = actualCompanies.get(0).getOwners().iterator().next();
		
		Assertions.assertThat(p3).isNotSameAs(p4);
		Assertions.assertThat(p3.getAddress()).isNotNull();
		Assertions.assertThat(p3.getBestFriend()).isNull();
		
		Assertions.assertThat(p4.getAddress()).isNull();
		Assertions.assertThat(p4.getBestFriend()).isNotNull();
	}
	@Test
	public void testBasicFetching() {
		
		PersistenceGmSession session = GmTestTools.newSession(access);
		
		List<Company> companies = seeder.getCompanies();
		Set<Object> companyIds = companies.stream().map(c -> c.getId()).collect(Collectors.toSet());
		
		Company companyPrototype = Fetching.graphPrototype(Company.T);
		
		companyPrototype.getAddress().getCity();
		companyPrototype.getContracts();
		companyPrototype.getOwners().iterator().next().getAddress().getCity();
		
		EntityGraphNode rootNode = Fetching.rootNode(companyPrototype);
		
		Set<Company> expectedCompanies = new HashSet<>(Fetching.fetchFromLocal(rootNode, companies));
		
		Set<Company> actualCompanies = new HashSet<>(session.query().entities(EntityQuery.create(Company.T).where(
				EntityQueries.in(
						EntityQueries.property(GenericEntity.id),
						companyIds))).list());
		
		Fetching.fetch(session, rootNode, actualCompanies);
		
		AssemblyComparisonResult comparisonResult = AssemblyComparison.build() //
				.enableTracking() //
				.compare(expectedCompanies, actualCompanies);
		
		Assertions.assertThat(comparisonResult.equal())//
		.describedAs(() -> comparisonResult.mismatchDescription() + " @ " + stringify(comparisonResult.firstMismatchPath())).isTrue();
		
	}
	
	private String stringify(IModelPathElement element) {
		StringBuilder builder = new StringBuilder();
		stringify(element, builder);
		return builder.toString();
	}

	/*
	Company@13.address.city.name
	Company@13.contracts[0].name
	Company@13.contracts[someString].name
	Company@13.contracts[someString].name
	Company@13.personToAddress[Person@1].street;
	Company@13.personToAddress(Person@1).address;
	*/
	
	private void stringify(IModelPathElement element, StringBuilder builder) {
		
		IModelPathElement previous = element.getPrevious();
		
		if (previous != null)
			stringify(previous, builder);
		
		switch (element.getElementType()) {
		case Root:
		case EntryPoint:
			builder.append(element.getType().getTypeSignature());
			break;
		case ListItem:
			builder.append('[');
			builder.append(((IListItemModelPathElement)element).getIndex());
			builder.append(']');
			break;
		case SetItem:
			builder.append('(');
			ISetItemModelPathElement setElement = (ISetItemModelPathElement)element;
			SetType setType = (SetType) getCollectionType(setElement);
			GenericModelType setElementType = setType.getCollectionElementType();
			builder.append(stringify(setElementType, setElement.getValue()));
			builder.append(')');
			break;
		case MapKey:
			break;
		case MapValue:
			IMapKeyModelPathElement mapKeyElement = (IMapKeyModelPathElement)element;
			MapType mapType = (MapType) getCollectionType(mapKeyElement);
			GenericModelType keyType = mapType.getKeyType();
			builder.append('[');
			builder.append(stringify(keyType, mapKeyElement.getValue()));
			builder.append(']');
			break;
		case Property:
			builder.append(".");
			builder.append(((IPropertyModelPathElement)element).getProperty().getName());
			break;
		}
	}
	
	private CollectionType getCollectionType(IPropertyRelatedModelPathElement element) {
		Property property = element.getProperty();
		
		if (property != null)
			return (CollectionType) property.getType();
			
		if (element instanceof ISetItemModelPathElement) {
			return EssentialCollectionTypes.TYPE_SET;
		}
		else if (element instanceof IMapKeyModelPathElement) {
			return EssentialCollectionTypes.TYPE_MAP;
		}
		else if (element instanceof IMapValueModelPathElement) {
			return EssentialCollectionTypes.TYPE_MAP;
		}
		else if (element instanceof IListItemModelPathElement) {
			return EssentialCollectionTypes.TYPE_LIST;
		}
		
		throw new IllegalStateException("unexpected element type " + element.getType());
	}
	
	private String stringify(GenericModelType type, Object value) {
		if (type.isBase())
			type = type.getActualType(value);

		if (type.isEntity()) {
			GenericEntity entity = (GenericEntity)value;
			return entity.entityType().getShortName() + "@" + entity.getId();
		}
		else {
			return String.valueOf(value);
		}

	}
	
}

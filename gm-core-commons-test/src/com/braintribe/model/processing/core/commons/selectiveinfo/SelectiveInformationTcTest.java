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
package com.braintribe.model.processing.core.commons.selectiveinfo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.braintribe.model.access.ModelAccessException;
import com.braintribe.model.access.smood.basic.SmoodAccess;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.TraversingCriterion;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.core.commons.SelectiveInformationSupport;
import com.braintribe.model.processing.core.commons.selectiveinfo.model.Address;
import com.braintribe.model.processing.core.commons.selectiveinfo.model.GmCoreCommonsTestModelProvider;
import com.braintribe.model.processing.core.commons.selectiveinfo.model.House;
import com.braintribe.model.processing.core.commons.selectiveinfo.model.HouseOwner;
import com.braintribe.model.processing.query.fluent.SelectQueryBuilder;
import com.braintribe.model.query.SelectQuery;
import com.braintribe.model.query.SelectQueryResult;
import com.braintribe.testing.tools.gm.GmTestTools;

/**
 * @author peter.gazdik
 */
public class SelectiveInformationTcTest {

	private final SmoodAccess smoodAccess = GmTestTools.newSmoodAccessMemoryOnly("test", GmCoreCommonsTestModelProvider.model);

	@Before
	public void initData() throws Exception {
		Address ownerAddress = Address.T.create();
		ownerAddress.setId(1L);
		ownerAddress.setStreet("Owner Street");

		HouseOwner houseOwner = HouseOwner.T.create();
		houseOwner.setId(2L);
		houseOwner.setAddress(ownerAddress);

		Address houseAddress = Address.T.create();
		houseAddress.setId(3L);
		houseAddress.setStreet("House Street");

		House house = House.T.create();
		house.setOwner(houseOwner);
		house.setAddress(houseAddress);

		smoodAccess.getDatabase().initialize(Arrays.asList(ownerAddress, houseOwner, houseAddress, house));
	}

	@Test
	public void notUsedPropertiesAbsent() throws Exception {
		House house = queryHouseWhenSelectiveInfoIs("Static");

		assertPropertyAbsent(house, "address");
		assertPropertyAbsent(house, "owner");
	}

	@Test
	public void usedPropertyLoaded() throws Exception {
		House house = queryHouseWhenSelectiveInfoIs("house in ${address}");

		assertPropertyPresent(house, "id");
		assertPropertyPresent(house, GenericEntity.partition);
		assertPropertyPresent(house, "address");
		assertPropertyAbsent(house, "owner");
	}

	@Test
	public void usedPropertyWithPathLoaded() throws Exception {
		House house = queryHouseWhenSelectiveInfoIs("house in ${address.street}");

		assertPropertyPresent(house, "id");
		assertPropertyPresent(house, GenericEntity.partition);
		assertPropertyPresent(house, "address");
		assertPropertyAbsent(house, "owner");
	}

	@Test
	public void usedPropertyWithTwoLevelPathLoaded() throws Exception {
		House house = queryHouseWhenSelectiveInfoIs("house in ${owner.address}");

		assertPropertyPresent(house, "id");
		assertPropertyPresent(house, GenericEntity.partition);
		assertPropertyAbsent(house, "address");
		assertPropertyPresent(house, "owner");

		HouseOwner owner = house.getOwner();
		assertPropertyPresent(owner, "id");
		assertPropertyPresent(owner, "address");
	}

	private House queryHouseWhenSelectiveInfoIs(String selectiveInformation) {
		TraversingCriterion tc = createTcForSelectiveInfoString(selectiveInformation);

		SelectQuery query = new SelectQueryBuilder().from(House.class, "h").tc(tc).done();

		List<?> entities = query(query).getResults();
		return (House) entities.get(0);
	}

	private static TraversingCriterion createTcForSelectiveInfoString(String selectiveInformation) {
		List<String[]> propertyChains = SelectiveInformationSupport.extractPropertyChains(selectiveInformation);
		return SelectiveInformationSupport.buildTcFor(propertyChains);
	}

	private SelectQueryResult query(SelectQuery query) {
		try {
			return smoodAccess.query(query);

		} catch (ModelAccessException e) {
			throw new RuntimeException("Query evaluation failed.", e);
		}
	}

	private void assertPropertyAbsent(GenericEntity entity, String propertyName) {
		Property property = entity.entityType().getProperty(propertyName);
		assertThat(property.getAbsenceInformation(entity)).isNotNull();
	}

	private void assertPropertyPresent(GenericEntity entity, String propertyName) {
		Property property = entity.entityType().getProperty(propertyName);
		assertThat(property.getAbsenceInformation(entity)).isNull();
	}

}

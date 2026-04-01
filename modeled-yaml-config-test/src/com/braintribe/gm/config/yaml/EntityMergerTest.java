package com.braintribe.gm.config.yaml;

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.braintribe.gm.config.yaml.model.MergedEntity;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.config.IncompatibleMergeTypes;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.AbsenceInformation;
import com.braintribe.model.generic.reflection.EntityType;

/**
 * Tests for {@link EntityMerger}
 * 
 * @author peter.gazdik
 */
public class EntityMergerTest {

	// ###########################################
	// ## . . . . . Simple Properties . . . . . ##
	// ###########################################

	@Test
	public void absentProperty_TakenFromDefaults() {
		MergedEntity eRoot = createAbsentEntity();

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.setString("default-value");

		merge(eRoot, dRoot);

		assertThat(eRoot.getString()).isEqualTo("default-value");
	}

	@Test
	public void presentProperty_NotOverridden() {
		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.setString("entity-value");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.setString("default-value");

		merge(eRoot, dRoot);

		assertThat(eRoot.getString()).isEqualTo("entity-value");
	}

	@Test
	public void nullProperty_NotOverridden() {
		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.setString(null); // explicitly set to null (not absent)

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.setString("default-value");

		merge(eRoot, dRoot);

		// null was explicitly set, so it's not absent - dRoot should not override it
		assertThat(eRoot.getString()).isNull();
	}

	@Test
	public void absentDefaultProperty_NoEffect() {
		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.setString("entity-value");

		MergedEntity dRoot = createAbsentEntity();

		merge(eRoot, dRoot);

		assertThat(eRoot.getString()).isEqualTo("entity-value");
	}

	// ###########################################
	// ## . . . . . . . . Lists . . . . . . . . ##
	// ###########################################

	@Test
	public void listStr_BothPresent_Concatenated() {
		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getListStr().add("a");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListStr().add("b");
		dRoot.getListStr().add("c");

		merge(eRoot, dRoot);

		assertThat(eRoot.getListStr()).containsExactly("a", "b", "c");
	}

	@Test
	public void listStr_AbsentEntity_CopiedFromDefaults() {
		MergedEntity eRoot = createAbsentEntity();

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListStr().add("x");

		merge(eRoot, dRoot);

		assertThat(eRoot.getListStr()).containsExactly("x");
	}

	@Test
	public void listEntity_DuplicateGidsAreSkipped() {
		// Entity
		MergedEntity eChild1 = createEntity("child-1");
		eChild1.setString("entity-child");

		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getListEntity().add(eChild1);

		// Defaults
		MergedEntity dChild1 = createEntity("child-1");
		dChild1.setString("default-child");

		MergedEntity dChild2 = createEntity("child-2");
		dChild2.setString("default-only");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dChild1);
		dRoot.getListEntity().add(dChild2);

		// Merge
		merge(eRoot, dRoot);

		assertThat(eRoot.getListEntity()).hasSize(2);
		assertThat(eRoot.getListEntity().get(0).getGlobalId()).isEqualTo("child-1");
		assertThat(eRoot.getListEntity().get(0).getString()).isEqualTo("entity-child");
		assertThat(eRoot.getListEntity().get(1).getGlobalId()).isEqualTo("child-2");
	}

	@Test
	public void listEntity_NullGidsAreAlwaysAdded() {
		MergedEntity eChild = MergedEntity.T.create(); // no globalId
		eChild.setString("entity-child");

		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getListEntity().add(eChild);

		MergedEntity dChild = MergedEntity.T.create(); // no globalId
		dChild.setString("default-child");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dChild);

		merge(eRoot, dRoot);

		assertThat(eRoot.getListEntity()).hasSize(2);
		assertThat(eRoot.getListEntity().get(0)).isSameAs(eChild);
		assertThat(eRoot.getListEntity().get(1)).isSameAs(dChild);
	}

	@Test
	public void setEntity_DuplicateGidsAreSkipped_UniqueGidsAdded() {
		MergedEntity eChild1 = createEntity("child-1");
		eChild1.setString("entity-child");

		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getSetEntity().add(eChild1);

		MergedEntity dChild1 = createEntity("child-1");
		dChild1.setString("default-child");

		MergedEntity dChild2 = createEntity("child-2");
		dChild2.setString("default-only");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getSetEntity().add(dChild1);
		dRoot.getSetEntity().add(dChild2);

		merge(eRoot, dRoot);

		assertThat(eRoot.getSetEntity()).hasSize(2);
		assertThat(eRoot.getSetEntity()).contains(eChild1, dChild2);
		assertThat(eRoot.getSetEntity()).doesNotContain(dChild1);
	}

	// ##########################################
	// ## . . . . . . . . Maps . . . . . . . . ##
	// ##########################################

	@Test
	public void mapStrInt_MissingKeysAreMergedFromDefaults() {
		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getMapStrInt().put("a", 1);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getMapStrInt().put("a", 99);
		dRoot.getMapStrInt().put("b", 2);

		merge(eRoot, dRoot);

		assertThat(eRoot.getMapStrInt()).containsEntry("a", 1);
		assertThat(eRoot.getMapStrInt()).containsEntry("b", 2);
		assertThat(eRoot.getMapStrInt()).hasSize(2);
	}

	@Test
	public void mapStrInt_AbsentEntity_CopiedFromDefaults() {
		MergedEntity eRoot = createAbsentEntity();

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getMapStrInt().put("key", 42);

		merge(eRoot, dRoot);

		assertThat(eRoot.getMapStrInt()).containsEntry("key", 42);
	}

	@Test
	public void mapEntEnt_DuplicateKeyGidsAreSkipped() {
		MergedEntity eKey = createEntity("key-1");
		MergedEntity eVal = MergedEntity.T.create();
		eVal.setString("entity-val");

		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getMapEntEnt().put(eKey, eVal);

		MergedEntity dKey = createEntity("key-1");
		MergedEntity dVal = MergedEntity.T.create();
		dVal.setString("default-val");

		MergedEntity dKey2 = createEntity("key-2");
		MergedEntity dVal2 = MergedEntity.T.create();
		dVal2.setString("default-val-2");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getMapEntEnt().put(dKey, dVal);
		dRoot.getMapEntEnt().put(dKey2, dVal2);

		merge(eRoot, dRoot);

		Map<String, String> map = eRoot.getMapEntEnt().entrySet().stream() //
				.collect(Collectors.toMap( //
						e -> e.getKey().getGlobalId(), // use globalId as key for easier assertions
						e -> e.getValue().getString() //
				));

		assertThat(map).hasSize(2) //
				.containsEntry("key-1", "entity-val") //
				.containsEntry("key-2", "default-val-2")//
		;
	}

	// ############################################
	// ## . . . . Deep Merge by GlobalId . . . . ##
	// ############################################

	@Test
	public void deepMerge() {
		MergedEntity entityChild = createEntity("shared-child");
		entityChild.setString("child-string");

		MergedEntity eRoot = createEntity("root");
		eRoot.getListEntity().add(entityChild);

		MergedEntity dChild = createAbsentEntity();
		dChild.setGlobalId("shared-child");
		dChild.setListStr(asList("from-default"));

		MergedEntity dRoot = createEntity("root");
		dRoot.getListEntity().add(dChild);

		merge(eRoot, dRoot);

		MergedEntity child = eRoot.getListEntity().get(0);

		assertThat(child).isSameAs(entityChild);
		assertThat(child.getString()).isEqualTo("child-string");
		assertThat(child.getListStr()).containsExactly("from-default");
	}

	@Test
	public void deepMerge_IncompatibleTypes() {
		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.getListEntity().add(eShared);

		// dRoot has an AbsenceInformation with the same globalId - incompatible type
		GenericEntity dShared = AbsenceInformation.T.create();
		dShared.setGlobalId("shared");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListBase().add(dShared);

		Reason reason = merge(eRoot, dRoot);
		assertThat(hasReasonOfType(reason, IncompatibleMergeTypes.T)).isTrue();
	}

	@Test
	public void deepMerge_DeepReplaceDefault_defaultsEnterViaCollectionMerge() {
		// Entity.base = Shared("entity-value")
		// Default.listEntity = dChild->.base=Shared("default-value")
		// Merged:
		// Entity.base = Shared("entity-value")
		// Entity.listEntity = dChild->.base=Shared("entity-value")

		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = MergedEntity.T.create();
		eRoot.setBase(eShared);

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild = MergedEntity.T.create();
		dChild.setBase(dShared);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dChild);

		merge(eRoot, dRoot);

		assertThat(eRoot.getBase()).isSameAs(eShared);

		List<MergedEntity> listEntity = eRoot.getListEntity();
		assertThat(listEntity).hasSize(1).contains(dChild);

		assertThat(dChild.getBase()).isSameAs(eShared);
	}

	@Test
	public void deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent() {
		// Entity.base = Shared("entity-value")
		// Default.listEntity = dChild->.base=Shared("default-value")
		// Merged:
		// Entity.base = Shared("entity-value")
		// Entity.listEntity = dChild ->.base=Shared("entity-value")

		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = createAbsentEntity();
		eRoot.setListBase(asList(eShared));

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild = MergedEntity.T.create();
		dChild.setBase(dShared);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dChild);

		merge(eRoot, dRoot);

		assertThat(eRoot.getListBase()).contains(eShared);

		List<MergedEntity> listEntity = eRoot.getListEntity();
		assertThat(listEntity).hasSize(1).contains(dChild);

		assertThat(dChild.getBase()).isSameAs(eShared);
	}

	@Test
	public void deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaBase() {
		// eRoot.base = Shared("entity-value")
		// dRoot.listEntity = dChild1->.base=dChild2->.base=Shared("default-value")
		// Merged:
		// eRoot.base = Shared("entity-value")
		// eRoot.listEntity = dChild1->.base=dChild2->.base=Shared("entity-value")

		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = createAbsentEntity();
		eRoot.setListBase(asList(eShared));

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild2 = createEntity("default-child-2");
		dChild2.setBase(dShared);

		MergedEntity dChild1 = createEntity("default-child-1");
		dChild1.setBase(dChild2);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dChild1);

		merge(eRoot, dRoot);

		assertThat(eRoot.getListBase()).contains(eShared);

		assertThat(eRoot.getListEntity()).containsExactly(dChild1);
		assertThat(dChild1.getBase()).isEqualTo(dChild2);

		assertThat(dChild2.getBase()).isSameAs(eShared);
	}

	@Test
	/** @see #deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaBase() */
	public void deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaList() {
		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = createAbsentEntity();
		eRoot.setListBase(asList(eShared));

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild2 = createEntity("default-child-2");
		dChild2.getListEntity().add(dShared);

		MergedEntity dChild1 = createEntity("default-child-1");
		dChild1.getListEntity().add(dChild2);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dChild1);

		merge(eRoot, dRoot);

		assertThat(eRoot.getListBase()).contains(eShared);

		assertThat(eRoot.getListEntity()).containsExactly(dChild1);
		assertThat(dChild1.getListEntity()).containsExactly(dChild2);

		assertThat(dChild2.getListEntity()).containsExactly(eShared);
	}

	@Test
	/** @see #deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaBase() */
	public void deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaSet() {
		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = createAbsentEntity();
		eRoot.setListBase(asList(eShared));

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild2 = createEntity("default-child-2");
		dChild2.getSetEntity().add(dShared);

		MergedEntity dChild1 = createEntity("default-child-1");
		dChild1.getSetEntity().add(dChild2);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getSetEntity().add(dChild1);

		merge(eRoot, dRoot);

		assertThat(eRoot.getListBase()).contains(eShared);

		assertThat(eRoot.getSetEntity()).containsExactly(dChild1);
		assertThat(dChild1.getSetEntity()).containsExactly(dChild2);

		assertThat(dChild2.getSetEntity()).containsExactly(eShared);
	}

	@Test
	/** @see #deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaBase() */
	public void deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaMapValue() {
		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = createEntity("entity");
		eRoot.setBase(eShared);

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild2 = createEntity("default-child-2");
		dChild2.getMapStringEnt().put("key", dShared);

		MergedEntity dChild1 = createEntity("default-child-1");
		dChild1.getMapStringEnt().put("key", dChild2);

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getMapStringEnt().put("key", dChild1);

		merge(eRoot, dRoot);

		assertThat(eRoot.getBase()).isSameAs(eShared);

		assertThat(eRoot.getMapStringEnt()).containsEntry("key", dChild1);
		assertThat(dChild1.getMapStringEnt()).containsEntry("key", dChild2);

		assertThat(dChild2.getMapStringEnt()).containsEntry("key", eShared);
	}

	@Test
	/** @see #deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaBase() */
	public void deepMerge_DeepReplaceDefault_defaultEntersAsEntityWasAbsent_nestedViaMapKey() {
		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		MergedEntity eRoot = createEntity("entity");
		eRoot.setBase(eShared);

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dChild2 = createEntity("default-child-2");
		dChild2.getMapEntString().put(dShared, "key");

		MergedEntity dChild1 = createEntity("default-child-1");
		dChild1.getMapEntString().put(dChild2, "key");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getMapEntString().put(dChild1, "key");

		merge(eRoot, dRoot);

		assertThat(eRoot.getBase()).isSameAs(eShared);

		assertThat(eRoot.getMapEntString()).containsEntry(dChild1, "key");
		assertThat(dChild1.getMapEntString()).containsEntry(dChild2, "key");

		assertThat(dChild2.getMapEntString()).containsEntry(eShared, "key");
	}

	@Test
	public void deepMerge_DeepReplaceDefault_viaAbsentListProperty() {
		// eRoot has eShared via base
		// dRoot has an absent-filling map whose value references dShared (same gid as eShared)
		// After merge, the map value should be sanitized to point to eShared

		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		// eRoot's mapStringEnt is not set (default empty), so absent filling kicks in via noticeDefaults
		MergedEntity eRoot = createAbsentEntity();
		eRoot.setBase(eShared);

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getListEntity().add(dShared);

		merge(eRoot, dRoot);

		assertThat(eRoot.getListEntity()).containsExactly(eShared);
	}

	@Test
	public void deepMerge_DeepReplaceDefault_viaAbsentMapProperty() {
		// eRoot has eShared via base
		// dRoot has an absent-filling map whose value references dShared (same gid as eShared)
		// After merge, the map value should be sanitized to point to eShared

		MergedEntity eShared = createEntity("shared");
		eShared.setString("entity-hello");

		// eRoot's mapStringEnt is not set (default empty), so absent filling kicks in via noticeDefaults
		MergedEntity eRoot = createAbsentEntity();
		eRoot.setBase(eShared);

		MergedEntity dShared = createEntity("shared");
		dShared.setString("default-hello");

		MergedEntity dRoot = MergedEntity.T.create();
		dRoot.getMapStringEnt().put("key", dShared);

		merge(eRoot, dRoot);

		assertThat(eRoot.getMapStringEnt()).containsEntry("key", eShared);
	}

	// ###########################################
	// ## . . . . . . . Helpers . . . . . . . . ##
	// ###########################################

	private MergedEntity createEntity(String gid) {
		MergedEntity eShared = MergedEntity.T.create();
		eShared.setGlobalId(gid);
		return eShared;
	}

	private Reason merge(MergedEntity eRoot, MergedEntity dRoot) {
		Maybe<GenericEntity> resultMaybe = EntityMerger.merge(eRoot, "test-e", dRoot);
		GenericEntity result = resultMaybe.value();
		assertThat(result).isSameAs(eRoot);

		return resultMaybe.whyUnsatisfied();
	}

	/**
	 * Creates a {@link MergedEntity} with all properties marked as absent (simulating an eRoot parsed from an empty YAML with
	 * absentifyMissingProperties).
	 */
	private static MergedEntity createAbsentEntity() {
		return ModeledYamlConfigurationTest.createAbsentEntity(MergedEntity.T);
	}

	private static boolean hasReasonOfType(Reason reason, EntityType<? extends Reason> type) {
		assertThat(reason).isNotNull();

		if (type.isInstance(reason))
			return true;

		for (Reason cause : reason.getReasons())
			if (hasReasonOfType(cause, type))
				return true;

		return false;
	}

}
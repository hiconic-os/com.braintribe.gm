package com.braintribe.gm.config.yaml.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.ToStringInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * @author peter.gazdik
 */
@ToStringInformation("MergeEntity[${globalId}]-${string}")
public interface MergedEntity extends GenericEntity {

	EntityType<MergedEntity> T = EntityTypes.T(MergedEntity.class);

	Object getBase();
	void setBase(Object base);

	String getString();
	void setString(String string);

	// List

	List<String> getListStr();
	void setListStr(List<String> listStr);

	List<MergedEntity> getListEntity();
	void setListEntity(List<MergedEntity> listEntity);

	List<Object> getListBase();
	void setListBase(List<Object> listBase);

	Set<MergedEntity> getSetEntity();
	void setSetEntity(Set<MergedEntity> SetEntity);

	// Map

	Map<String, Integer> getMapStrInt();
	void setMapStrInt(Map<String, Integer> mapStrInt);

	Map<MergedEntity, MergedEntity> getMapEntEnt();
	void setMapEntEnt(Map<MergedEntity, MergedEntity> mapEntEnt);

	Map<String, MergedEntity> getMapStringEnt();
	void setMapStringEnt(Map<String, MergedEntity> mapStringEnt);

	Map<MergedEntity, String> getMapEntString();
	void setMapEntString(Map<MergedEntity, String> mapEntString);

	Map<Object, Object> getMapBaseBase();
	void setMapBaseBase(Map<Object, Object> mapBaseBase);

}

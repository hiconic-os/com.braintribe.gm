// Entityh.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityh extends HasScalars {

    EntityType<Entityh> T = EntityTypes.T(Entityh.class);

    // TO_ONE
    String entityi = "entityi";
    String entityj = "entityj";

    Entityi getEntityi();
    void setEntityi(Entityi entityi);

    Entityj getEntityj();
    void setEntityj(Entityj entityj);

    // Collections
    // Map<Entitye, boolean> -> Map<Entitye, Boolean>
    String mapEntityjBoolean = "mapEntityjBoolean";
    Map<Entityj, Boolean> getMapEntityjBoolean();
    void setMapEntityjBoolean(Map<Entityj, Boolean> mapEntityjBoolean);

    // List<float> -> Float
    String listFloat = "listFloat";
    List<Float> getListFloat();
    void setListFloat(List<Float> listFloat);
}

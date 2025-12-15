// Entityd.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityd extends HasScalars {

    EntityType<Entityd> T = EntityTypes.T(Entityd.class);

    // TO_ONE
    String entitye = "entitye";
    String entityf = "entityf";
    String entityg = "entityg";
    String entityh = "entityh";

    Entitye getEntitye();
    void setEntitye(Entitye entitye);

    Entityf getEntityf();
    void setEntityf(Entityf entityf);

    Entityg getEntityg();
    void setEntityg(Entityg entityg);

    Entityh getEntityh();
    void setEntityh(Entityh entityh);

    // Collections
    // Map<Entityb, Boolean>
    String mapEntitybBoolean = "mapEntitybBoolean";
    Map<Entityb, Boolean> getMapEntitybBoolean();
    void setMapEntitybBoolean(Map<Entityb, Boolean> mapEntitybBoolean);

    // Set<Entityi>
    String setEntityi = "setEntityi";
    Set<Entityi> getSetEntityi();
    void setSetEntityi(Set<Entityi> setEntityi);
}

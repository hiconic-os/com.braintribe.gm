// Entityb.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.Map;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityb extends HasScalars {

    EntityType<Entityb> T = EntityTypes.T(Entityb.class);

    // TO_ONE
    String entityc = "entityc";
    String entityd = "entityd";
    String entitye = "entitye";
    String entityf = "entityf";

    Entityc getEntityc();
    void setEntityc(Entityc entityc);

    Entityd getEntityd();
    void setEntityd(Entityd entityd);

    Entitye getEntitye();
    void setEntitye(Entitye entitye);

    Entityf getEntityf();
    void setEntityf(Entityf entityf);

    // Collections
    // Map<String, Entityg>
    String mapStringEntityg = "mapStringEntityg";
    Map<String, Entityg> getMapStringEntityg();
    void setMapStringEntityg(Map<String, Entityg> mapStringEntityg);

    // Set<Double> -> Double
    String setDouble = "setDouble";
    Set<Double> getSetDouble();
    void setSetDouble(Set<Double> setDouble);
}

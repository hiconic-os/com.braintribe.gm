// Entitye.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entitye extends HasScalars {

    EntityType<Entitye> T = EntityTypes.T(Entitye.class);

    // TO_ONE
    String entityf = "entityf";
    String entityg = "entityg";
    String entityh = "entityh";
    String entityi = "entityi";

    Entityf getEntityf();
    void setEntityf(Entityf entityf);

    Entityg getEntityg();
    void setEntityg(Entityg entityg);

    Entityh getEntityh();
    void setEntityh(Entityh entityh);

    Entityi getEntityi();
    void setEntityi(Entityi entityi);

    // Collections
    // Map<String, Entityj> -> BigDecimal
    String mapStringEntityj = "mapStringEntityj";
    Map<String, Entityj> getMapStringEntityj();
    void setMapStringEntityj(Map<String, Entityj> mapStringEntityj);

    // List<integer> -> Integer
    String listInteger = "listInteger";
    List<Integer> getListInteger();
    void setListInteger(List<Integer> listInteger);
}

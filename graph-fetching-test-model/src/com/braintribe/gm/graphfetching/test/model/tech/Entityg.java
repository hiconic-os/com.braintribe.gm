// Entityg.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityg extends HasScalars {

    EntityType<Entityg> T = EntityTypes.T(Entityg.class);

    // TO_ONE
    String entityh = "entityh";
    String entityi = "entityi";
    String entityj = "entityj";

    Entityh getEntityh();
    void setEntityh(Entityh entityh);

    Entityi getEntityi();
    void setEntityi(Entityi entityi);

    Entityj getEntityj();
    void setEntityj(Entityj entityj);

    // Collections
    // Map<string, string> -> Map<String, String>
    String mapStringString = "mapStringString";
    Map<String, String> getMapStringString();
    void setMapStringString(Map<String, String> mapStringString);

    // List<Entityb>
    String listEntityi = "listEntityi";
    List<Entityi> getListEntityi();
    void setListEntityi(List<Entityi> listEntityi);
}

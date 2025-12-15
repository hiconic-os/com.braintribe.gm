// Entityc.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entityc extends HasScalars {

    EntityType<Entityc> T = EntityTypes.T(Entityc.class);

    // TO_ONE
    String entityd = "entityd";
    String entitye = "entitye";
    String entityf = "entityf";
    String entityg = "entityg";

    Entityd getEntityd();
    void setEntityd(Entityd entityd);

    Entitye getEntitye();
    void setEntitye(Entitye entitye);

    Entityf getEntityf();
    void setEntityf(Entityf entityf);

    Entityg getEntityg();
    void setEntityg(Entityg entityg);

    // Collections
    // Map<Enuma, Entityh>
    String mapEnumaEntityh = "mapEnumaEntityh";
    Map<Enuma, Entityh> getMapEnumaEntityh();
    void setMapEnumaEntityh(Map<Enuma, Entityh> mapEnumaEntityh);

    // List<boolean> -> Boolean
    String listBoolean = "listBoolean";
    List<Boolean> getListBoolean();
    void setListBoolean(List<Boolean> listBoolean);
}

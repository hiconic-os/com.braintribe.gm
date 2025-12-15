// Entitya.java
package com.braintribe.gm.graphfetching.test.model.tech;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Entitya extends HasScalars {

    EntityType<Entitya> T = EntityTypes.T(Entitya.class);

    // TO_ONE
    String entityb = "entityb";
    String entityc = "entityc";
    String entityd = "entityd";
    String entitye = "entitye";

    Entityb getEntityb();
    void setEntityb(Entityb entityb);

    Entityc getEntityc();
    void setEntityc(Entityc entityc);

    Entityd getEntityd();
    void setEntityd(Entityd entityd);

    Entitye getEntitye();
    void setEntitye(Entitye entitye);

    // Collections
    // Set<Enuma>
    String setEnuma = "setEnuma";
    Set<Enuma> getSetEnuma();
    void setSetEnuma(Set<Enuma> setEnuma);

    // List<Entityf>
    String listEntityf = "listEntityf";
    List<Entityf> getListEntityf();
    void setListEntityf(List<Entityf> listEntityf);
}

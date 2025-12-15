package com.braintribe.gm.graphfetching.test.model.tech;

import java.math.BigDecimal;
import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface HasScalars extends GenericEntity {

    EntityType<HasScalars> T = EntityTypes.T(HasScalars.class);

    // ---- String ----
    String stringVal = "stringVal";
    String getStringVal();
    void setStringVal(String stringVal);

    // ---- Enum ----
    String enumVal = "enumVal";
    Enuma getEnumVal();
    void setEnumVal(Enuma enumVal);

    // ---- double ----
    String doubleVal = "doubleVal";
    Double getDoubleVal();
    void setDoubleVal(Double doubleVal);

    // ---- float ----
    String floatVal = "floatVal";
    Float getFloatVal();
    void setFloatVal(Float floatVal);

    // ---- decimal (BigDecimal) ----
    String decimalVal = "decimalVal";
    BigDecimal getDecimalVal();
    void setDecimalVal(BigDecimal decimalVal);

    // ---- long ----
    String longVal = "longVal";
    Long getLongVal();
    void setLongVal(Long longVal);

    // ---- integer ----
    String intVal = "intVal";
    Integer getIntVal();
    void setIntVal(Integer intVal);

    // ---- boolean ----
    String boolVal = "boolVal";
    Boolean getBoolVal();
    void setBoolVal(Boolean boolVal);

    // ---- Date ----
    String dateVal = "dateVal";
    Date getDateVal();
    void setDateVal(Date dateVal);
}


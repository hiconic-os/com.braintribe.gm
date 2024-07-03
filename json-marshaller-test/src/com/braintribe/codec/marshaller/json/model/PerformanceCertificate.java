// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.codec.marshaller.json.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface PerformanceCertificate extends GenericEntity {

	EntityType<PerformanceCertificate> T = EntityTypes.T(PerformanceCertificate.class);
	
	String lnNummer = "lnNummer";
	String kundeMail = "kundeMail";
	String kundeMailOptional = "kundeMailOptional";
	String kundenfeedback = "kundenfeedback";
	String auftragsnummer = "auftragsnummer";
	String tatigkeitsnummer = "tatigkeitsnummer";
	String leistungsdatum = "leistungsdatum";
	String durchgefuhrteArbeit = "durchgefuhrteArbeit";
	String standort = "standort";
	String abteilung = "abteilung";
	String fahrerName = "fahrerName";
	String fahrerEmail = "fahrerEmail";
	String naechtigung = "naechtigung";
	String uhrzeitVerlassen = "uhrzeitVerlassen";
	String zielland = "zielland";
	String zeitRueckkehr = "zeitRueckkehr";
	String dokument = "dokument";
	
	//lnNumber
	String getLnNummer();
	void setLnNummer(String lnNummer);
	
	//customerMail
	String getKundeMail();
	void setKundeMail(String kundeMail);
	
	//customerMailOptional
	String getKundeMailOptional();
	void setKundeMailOptional(String kundeMailOptional);
	
	//customerFeedback
	Integer getKundenfeedback();
	void setKundenfeedback(Integer kundenfeedback);
	
	//orderNumber
	String getAuftragsnummer();
	void setAuftragsnummer(String auftragsnummer);
	
	//actionNumber
	String getTatigkeitsnummer();
	void setTatigkeitsnummer(String tatigkeitsnummer);
	
	//serviceDate
	String getLeistungsdatum();
	void setLeistungsdatum(String leistungsdatum);
	
	//workDone
	String getDurchgefuhrteArbeit();
	void setDurchgefuhrteArbeit(String durchgefuhrteArbeit);
	
	//location
	String getStandort();
	void setStandort(String standort);
	
	//department
	String getAbteilung();
	void setAbteilung(String abteilung);
	
	//driverName
	String getFahrerName();
	void setFahrerName(String fahrerName);
	
	//driverEmail
	String getFahrerEmail();
	void setFahrerEmail(String fahrerEmail);
	
	//overnightStay
	String getNaechtigung(); 
	void setNaechtigung(String naechtigung); 
	
	//timeLeave
	String getUhrzeitVerlassen();
	void setUhrzeitVerlassen(String uhrzeitVerlassen);
	
	//destinationCountry
	String getZielland();
	void setZielland(String zielland);
	
	//timeReturn
	String getZeitRueckkehr();
	void setZeitRueckkehr(String zeitRueckkehr);
	
	//document
	String getDokument();
	void setDokument(String dokument);
}


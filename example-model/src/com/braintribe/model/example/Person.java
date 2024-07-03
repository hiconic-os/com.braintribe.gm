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
package com.braintribe.model.example;

import java.util.Date;

import com.braintribe.model.generic.StandardIdentifiable;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;

@SelectiveInformation("${name} ${lastname}")

public interface Person extends StandardIdentifiable {

	EntityType<Person> T = EntityTypes.T(Person.class);

	String name = "name";
	String lastname = "lastname";
	String company = "company";
	String birthday = "birthday";
	String address = "address";
	String contractNumber = "contractNumber";
	String personalNumber = "personalNumber";
	String nationality = "nationality";
	String costLocation = "costLocation";
	String svnr = "svnr";
	String phoneNumber = "phoneNumber";
	String email = "email";
	String position = "position";
	String superior = "superior";
	String probationTime = "probationTime";
	String endOfProbationTime = "endOfProbationTime";
	String evaluation = "evaluation";
	String hiringDate = "hiringDate";
	String resignationDate = "resignationDate";
	String gender = "gender";
	String status = "status";
	String familyStatus = "familyStatus";
	String image = "image";
	String contract = "contract";
	String premium = "premium";
	String payslip = "payslip";

	void setImage(Resource image);
	Resource getImage();

	String getName();
	void setName(String name);

	Company getCompany();
	void setCompany(Company company);

	Address getAddress();
	void setAddress(Address address);

	String getLastname();
	void setLastname(String lastname);

	Date getBirthday();
	void setBirthday(Date birthday);

	String getContractNumber();
	void setContractNumber(String contractNumber);

	String getSvnr();
	void setSvnr(String svnr);

	Person getSuperior();
	void setSuperior(Person superior);

	Boolean getProbationTime();
	void setProbationTime(Boolean probationTime);

	Date getEndOfProbationTime();
	void setEndOfProbationTime(Date endOfProbationTime);

	Date getHiringDate();
	void setHiringDate(Date hiringDate);

	Date getResignationDate();
	void setResignationDate(Date resignationDate);

	Gender getGender();
	void setGender(Gender gender);

	FamilyStatus getFamilyStatus();
	void setFamilyStatus(FamilyStatus familyStatus);

	String getEvaluation();
	void setEvaluation(String evaluation);

	Status getStatus();
	void setStatus(Status status);

	void setPersonalNumber(String personalNumber);
	String getPersonalNumber();

	String getNationality();
	void setNationality(String nationality);

	String getCostLocation();
	void setCostLocation(String costLocation);

	void setContract(String contract);
	String getContract();

	void setPayslip(String payslip);
	String getPayslip();

	void setPremium(String premium);
	String getPremium();

	String getPhoneNumber();
	void setPhoneNumber(String phoneNumber);

	String getEmail();
	void setEmail(String email);

	String getPosition();
	void setPosition(String position);

}

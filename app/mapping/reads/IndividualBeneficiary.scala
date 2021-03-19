/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.reads

import mapping.registration.IdentificationMapper.buildPassport
import models.IdentificationType
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.{PassportOrIdCardDetails, RoleInCompany}
import play.api.libs.json.{Format, Json}

import java.time.LocalDate


final case class IndividualBeneficiary(name: FullName,
                                       roleInCompany: Option[RoleInCompany],
                                       dateOfBirth: Option[LocalDate],
                                       nationalInsuranceNumber: Option[String],
                                       passportDetails: Option[PassportOrIdCardDetails],
                                       idCardDetails: Option[PassportOrIdCardDetails],
                                       ukAddress: Option[UKAddress],
                                       internationalAddress: Option[InternationalAddress],
                                       vulnerableYesNo: Option[Boolean],
                                       income: Option[Int] ,
                                       incomeYesNo: Option[Boolean],
                                       countryOfResidence: Option[String],
                                       countryOfNationality: Option[String],
                                       mentalCapacityYesNo: Option[Boolean]) extends BeneficiaryWithAddress {

  val identification: Option[IdentificationType] = (nationalInsuranceNumber, ukOrInternationalAddress, passportDetails, idCardDetails) match {
    case (None, None, None, None) => None
    case (Some(_), _, _, _) => Some(IdentificationType(nationalInsuranceNumber, None, None))
    case _ => Some(IdentificationType(None, buildValue(passportDetails, idCardDetails)(buildPassport), ukOrInternationalAddress))
  }
}

object IndividualBeneficiary {
  implicit val classFormat: Format[IndividualBeneficiary] = Json.format[IndividualBeneficiary]
}

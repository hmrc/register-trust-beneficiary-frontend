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

package mapping.registration

import mapping.reads.{IndividualBeneficiaries, IndividualBeneficiary}
import models.registration.pages.PassportOrIdCardDetails
import models.{IdentificationType, IndividualDetailsType, PassportType}
import pages.QuestionPage

class IndividualBeneficiaryMapper extends Mapper[IndividualDetailsType, IndividualBeneficiary] {

  override def section: QuestionPage[List[IndividualBeneficiary]] = IndividualBeneficiaries

  override def beneficiaryType(beneficiary: IndividualBeneficiary): IndividualDetailsType = IndividualDetailsType(
    name = beneficiary.name,
    dateOfBirth = beneficiary.dateOfBirth,
    vulnerableBeneficiary = beneficiary.vulnerableYesNo,
    beneficiaryType = beneficiary.roleInCompany.map(_.toString),
    beneficiaryDiscretion = beneficiary.incomeYesNo,
    beneficiaryShareOfIncome = beneficiary.income.map(_.toString),
    identification = identificationMap(beneficiary),
    countryOfResidence = beneficiary.countryOfResidence,
    nationality = beneficiary.countryOfNationality,
    legallyIncapable = beneficiary.mentalCapacityYesNo.map(!_)
  )

  private def identificationMap(indBen: IndividualBeneficiary): Option[IdentificationType] = {
    val nino = indBen.nationalInsuranceNumber
    val address = indBen.ukOrInternationalAddress
    val passport = indBen.passportDetails
    val idCard = indBen.idCardDetails

    (nino, address, passport, idCard) match {
      case (None, None, None, None) =>
        None
      case (Some(_), _, _, _) =>
        Some(IdentificationType(
          nino = nino,
          passport = None,
          address = None
        ))
      case (_, _, _, _) =>
        Some(IdentificationType(
          nino = None,
          passport = buildPassportOrIdCard(indBen.passportDetails, indBen.idCardDetails),
          address = address
        ))
    }
  }

  private def buildPassportOrIdCard(passport: Option[PassportOrIdCardDetails], idCardDetails: Option[PassportOrIdCardDetails]): Option[PassportType] =
    (passport, idCardDetails) match {
      case (Some(passport), _) => buildPassport(passport)
      case (_, Some(idCard)) => buildPassport(idCard)
      case (None, None) => None
    }

  private def buildPassport(details: PassportOrIdCardDetails): Option[PassportType] =
    Some(PassportType(details.cardNumber, details.expiryDate, details.country))

}

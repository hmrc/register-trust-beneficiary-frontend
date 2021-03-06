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

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.IndividualBeneficiary
import models.UserAnswers
import models.registration.pages.PassportOrIdCardDetails

class IndividualBeneficiaryMapper @Inject()(addressMapper: AddressMapper) extends Mapping[List[IndividualDetailsType]] {
  override def build(userAnswers: UserAnswers): Option[List[IndividualDetailsType]] = {

    val individualBeneficiaries : List[mapping.reads.IndividualBeneficiary] =
      userAnswers.get(mapping.reads.IndividualBeneficiaries).getOrElse(List.empty)

    removeIncompleteBeneficiaries(individualBeneficiaries, userAnswers) match {
      case Nil => None
      case list =>
        Some(
          list.map { indBen =>
            IndividualDetailsType(
              name = indBen.name,
              dateOfBirth = indBen.dateOfBirth,
              vulnerableBeneficiary = indBen.vulnerableYesNo,
              beneficiaryType = indBen.roleInCompany.map(_.toString),
              beneficiaryDiscretion = indBen.incomeYesNo,
              beneficiaryShareOfIncome = indBen.income.map(_.toString),
              identification = identificationMap(indBen),
              countryOfResidence = indBen.countryOfResidence,
              nationality = indBen.countryOfNationality,
              legallyIncapable = indBen.mentalCapacityYesNo.map(!_)
            )
          }
        )
    }
  }

  def removeIncompleteBeneficiaries(beneficiaries: List[IndividualBeneficiary], userAnswers: UserAnswers): List[IndividualBeneficiary] = {
    beneficiaries.filter(indDetailsType =>
      (userAnswers.is5mldEnabled, userAnswers.isTaxable) match {
        case (true, true) =>
            indDetailsType.incomeYesNo.isDefined &&
            indDetailsType.vulnerableYesNo.isDefined &&
            indDetailsType.mentalCapacityYesNo.isDefined
        case (true, false) =>
            indDetailsType.mentalCapacityYesNo.isDefined
        case (false, _) => true
      }
    )
  }


  private def identificationMap(indBen: IndividualBeneficiary): Option[IdentificationType] = {
    val nino = indBen.nationalInsuranceNumber
    val address = (indBen.ukAddress, indBen.internationalAddress) match {
      case (None, None) => None
      case (Some(address), _) => addressMapper.build(address)
      case (_, Some(address)) => addressMapper.build(address)
    }
    val passport = indBen.passportDetails
    val idCard = indBen.idCardDetails
     (nino, address, passport, idCard) match {
       case (None, None, None, None) => None
       case (Some(_), _, _, _) => Some(IdentificationType(nino, None, None))
       case (_, _, _, _) =>
         Some(IdentificationType(
           nino = None,
           passport = buildPassportOrIdCard(indBen.passportDetails, indBen.idCardDetails),
           address = address
         )
       )
     }
  }

  private def buildPassportOrIdCard(passport: Option[PassportOrIdCardDetails], idCardDetails: Option[PassportOrIdCardDetails]) =
    (passport, idCardDetails) match {
      case (Some(passport), _) => buildPassport(passport)
      case (_, Some(idCard)) => buildPassport(idCard)
      case (None, None) => None
    }

  private def buildPassport(details: PassportOrIdCardDetails) =
      Some(PassportType(details.cardNumber, details.expiryDate, details.country))
}

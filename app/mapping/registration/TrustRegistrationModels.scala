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

import java.time.LocalDate

import models.core.pages.FullName
import play.api.libs.json._

/**
  * Trust Registration API Schema - definitions models below
  */

case class BeneficiaryType(individualDetails: Option[List[IndividualDetailsType]],
                           company: Option[List[CompanyType]],
                           trust: Option[List[BeneficiaryTrustType]],
                           charity: Option[List[CharityType]],
                           unidentified: Option[List[UnidentifiedType]],
                           large: Option[List[LargeType]],
                           other: Option[List[OtherType]])

object BeneficiaryType {
  implicit val beneficiaryTypeFormat: Format[BeneficiaryType] = Json.format[BeneficiaryType]
}

case class IndividualDetailsType(name: FullName,
                                 dateOfBirth: Option[LocalDate],
                                 vulnerableBeneficiary: Boolean,
                                 beneficiaryType: Option[String],
                                 beneficiaryDiscretion: Boolean,
                                 beneficiaryShareOfIncome: Option[String],
                                 identification: Option[IdentificationType],
                                 countryOfResidence: Option[String],
                                 nationality: Option[String],
                                 legallyIncapable: Option[Boolean])

object IndividualDetailsType {

  implicit val individualDetailsTypeFormat: Format[IndividualDetailsType] = Json.format[IndividualDetailsType]
}

case class BeneficiaryTrustType(organisationName: String,
                                beneficiaryDiscretion: Option[Boolean],
                                beneficiaryShareOfIncome: Option[String],
                                identification: Option[IdentificationOrgType],
                                countryOfResidence: Option[String])

object BeneficiaryTrustType {
  implicit val beneficiaryTrustTypeFormat: Format[BeneficiaryTrustType] = Json.format[BeneficiaryTrustType]
}

case class IdentificationOrgType(utr: Option[String],
                                          address: Option[AddressType])

object IdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class Identification(nino: Option[String],
                          address: Option[AddressType])

object Identification {
  implicit val identificationFormat: Format[Identification] = Json.format[Identification]
}

case class CharityType(organisationName: String,
                       beneficiaryDiscretion: Option[Boolean],
                       beneficiaryShareOfIncome: Option[String],
                       identification: Option[IdentificationOrgType],
                       countryOfResidence: Option[String])

object CharityType {
  implicit val charityTypeFormat: Format[CharityType] = Json.format[CharityType]
}

case class TrustBeneficiaryCharityIdentification(utr: Option[String],
                                                 address: Option[AddressType])

object TrustBeneficiaryCharityIdentification {
  implicit val trustBeneficiaryCharityIdentificationFormat: Format[TrustBeneficiaryCharityIdentification] = Json.format[TrustBeneficiaryCharityIdentification]
}

case class UnidentifiedType(description: String,
                            beneficiaryDiscretion: Option[Boolean],
                            beneficiaryShareOfIncome: Option[String])

object UnidentifiedType {
  implicit val unidentifiedTypeFormat: Format[UnidentifiedType] = Json.format[UnidentifiedType]
}

case class LargeType(organisationName: String,
                     description: String,
                     description1: Option[String],
                     description2: Option[String],
                     description3: Option[String],
                     description4: Option[String],
                     numberOfBeneficiary: String,
                     identification: Option[IdentificationOrgType],
                     beneficiaryDiscretion: Option[Boolean],
                     beneficiaryShareOfIncome: Option[String],
                     countryOfResidence: Option[String])

object LargeType {
  implicit val largeTypeFormat: Format[LargeType] = Json.format[LargeType]
}

case class OtherType(description: String,
                     address: Option[AddressType],
                     beneficiaryDiscretion: Option[Boolean],
                     beneficiaryShareOfIncome: Option[String],
                     countryOfResidence: Option[String]
                    )

object OtherType {
  implicit val otherTypeFormat: Format[OtherType] = Json.format[OtherType]
}

case class CompanyType(organisationName: String,
                       beneficiaryDiscretion: Option[Boolean],
                       beneficiaryShareOfIncome: Option[String],
                       identification: Option[IdentificationOrgType],
                       countryOfResidence: Option[String])

object CompanyType {
  implicit val companyTypeFormat: Format[CompanyType] = Json.format[CompanyType]
}

case class IdentificationType(nino: Option[String],
                              passport: Option[PassportType],
                              address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class PassportType(number: String,
                        expirationDate: LocalDate,
                        countryOfIssue: String)

object PassportType {

  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String)

object AddressType {
  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}

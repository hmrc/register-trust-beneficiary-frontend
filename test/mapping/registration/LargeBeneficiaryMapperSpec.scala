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

import base.SpecBase
import generators.Generators
import models.{AddressType, IdentificationOrgType, LargeType}
import models.core.pages.{Description, InternationalAddress, UKAddress}
import models.registration.pages.HowManyBeneficiaries
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.CountryOfResidenceYesNoPage
import utils.Constants._

class LargeBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val mapper = injector.instanceOf[LargeBeneficiaryMapper]
  private val index0 = 0
  private val index1 = 1

  "LargeBeneficiariesMapper" when {

    "when user answers is empty" must {

      "must return None" in {

        val userAnswers = emptyUserAnswers

        mapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "return mapped data" when {

        "in 5mld mode with UK country of residence set" in {
          val userAnswers =
            emptyUserAnswers
              .set(LargeBeneficiaryNamePage(index0), "Employment Related Name").success.value
              .set(CountryOfResidenceYesNoPage(index0), true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage(index0), true).success.value
              .set(LargeBeneficiaryAddressYesNoPage(index0), false).success.value
              .set(LargeBeneficiaryDescriptionPage(index0), Description("Description", None, None, None, None)).success.value
              .set(LargeBeneficiaryNumberOfBeneficiariesPage(index0), HowManyBeneficiaries.Over1).success.value

          val large = mapper.build(userAnswers)

          large mustBe defined
          large.value.head mustBe LargeType(
            organisationName = "Employment Related Name",
            description = "Description",
            description1 = None,
            description2 = None,
            description3 = None,
            description4 = None,
            numberOfBeneficiary = "1",
            identification = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            countryOfResidence = Some(GB)
          )
        }

        "in 5mld mode with Non UK country of residence set" in {
          val userAnswers =
            emptyUserAnswers
              .set(LargeBeneficiaryNamePage(index0), "Employment Related Name").success.value
              .set(CountryOfResidenceYesNoPage(index0), true).success.value
              .set(CountryOfResidenceInTheUkYesNoPage(index0), false).success.value
              .set(CountryOfResidencePage(index0), "FR").success.value
              .set(LargeBeneficiaryAddressYesNoPage(index0), false).success.value
              .set(LargeBeneficiaryDescriptionPage(index0), Description("Description", None, None, None, None)).success.value
              .set(LargeBeneficiaryNumberOfBeneficiariesPage(index0), HowManyBeneficiaries.Over1).success.value

          val large = mapper.build(userAnswers)

          large mustBe defined
          large.value.head mustBe LargeType(
            organisationName = "Employment Related Name",
            description = "Description",
            description1 = None,
            description2 = None,
            description3 = None,
            description4 = None,
            numberOfBeneficiary = "1",
            identification = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            countryOfResidence = Some("FR")
          )
        }

        "No address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(LargeBeneficiaryNamePage(index0), "Employment Related Name").success.value
              .set(LargeBeneficiaryAddressYesNoPage(index0), false).success.value
              .set(LargeBeneficiaryDescriptionPage(index0), Description("Description", None, None, None, None)).success.value
              .set(LargeBeneficiaryNumberOfBeneficiariesPage(index0), HowManyBeneficiaries.Over1).success.value

          val large = mapper.build(userAnswers)

          large mustBe defined
          large.value.head mustBe LargeType(
            organisationName = "Employment Related Name",
            description = "Description",
            description1 = None,
            description2 = None,
            description3 = None,
            description4 = None,
            numberOfBeneficiary = "1",
            identification = None,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            countryOfResidence = None
          )
        }


        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(LargeBeneficiaryNamePage(index0), "Employment Related Name").success.value
              .set(LargeBeneficiaryAddressYesNoPage(index0), true).success.value
              .set(LargeBeneficiaryAddressUKYesNoPage(index0), true).success.value
              .set(LargeBeneficiaryAddressPage(index0),
                UKAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
              .set(LargeBeneficiaryDescriptionPage(index0), Description("Description", None, None, None, None)).success.value
              .set(LargeBeneficiaryNumberOfBeneficiariesPage(index0), HowManyBeneficiaries.Over1).success.value

          val large = mapper.build(userAnswers)

          large mustBe defined
          large.value.head mustBe LargeType(
            organisationName = "Employment Related Name",
            description = "Description",
            description1 = None,
            description2 = None,
            description3 = None,
            description4 = None,
            numberOfBeneficiary = "1",
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), GB)
              )
            )),
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            countryOfResidence = None
          )
        }

        "International Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(LargeBeneficiaryNamePage(index0), "Employment Related Name").success.value
              .set(LargeBeneficiaryAddressYesNoPage(index0), true).success.value
              .set(LargeBeneficiaryAddressUKYesNoPage(index0), false).success.value
              .set(LargeBeneficiaryAddressInternationalPage(index0),
                InternationalAddress("Line1", "Line2", Some("Line3"), "US")).success.value
              .set(LargeBeneficiaryDescriptionPage(index0), Description("Description", None, None, None, None)).success.value
              .set(LargeBeneficiaryNumberOfBeneficiariesPage(index0), HowManyBeneficiaries.Over201).success.value

          val large = mapper.build(userAnswers)

          large mustBe defined
          large.value.head mustBe LargeType(
            organisationName = "Employment Related Name",
            description = "Description",
            description1 = None,
            description2 = None,
            description3 = None,
            description4 = None,
            numberOfBeneficiary = "201",
            identification = Some(IdentificationOrgType(
              None,
              address = Some(
                AddressType("Line1", "Line2", Some("Line3"), None, None, "US")
              )
            )),
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            countryOfResidence = None
          )
        }

      }

      "must be able to create multiple Large beneficiaries" in {
        val userAnswers =
          emptyUserAnswers
            .set(LargeBeneficiaryNamePage(index0), "Employment Related Name 1").success.value
            .set(LargeBeneficiaryAddressYesNoPage(index0), false).success.value
            .set(LargeBeneficiaryDescriptionPage(index0), Description("Description", None, None, None, None)).success.value
            .set(LargeBeneficiaryNumberOfBeneficiariesPage(index0), HowManyBeneficiaries.Over1).success.value

            .set(LargeBeneficiaryNamePage(index1), "Employment Related Name 2").success.value
            .set(LargeBeneficiaryAddressYesNoPage(index1), true).success.value
            .set(LargeBeneficiaryAddressUKYesNoPage(index1), true).success.value
            .set(LargeBeneficiaryAddressPage(index1),
              UKAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
            .set(LargeBeneficiaryDescriptionPage(index1), Description("Description", None, None, None, None)).success.value
            .set(LargeBeneficiaryNumberOfBeneficiariesPage(index1), HowManyBeneficiaries.Over501).success.value


        val large = mapper.build(userAnswers)

        large mustBe defined
        large.value mustBe
          List(
            LargeType(
              organisationName = "Employment Related Name 1",
              description = "Description",
              description1 = None,
              description2 = None,
              description3 = None,
              description4 = None,
              numberOfBeneficiary = "1",
              identification = None,
              beneficiaryDiscretion = None,
              beneficiaryShareOfIncome = None,
              countryOfResidence = None
            ),

            LargeType(
              organisationName = "Employment Related Name 2",
              description = "Description",
              description1 = None,
              description2 = None,
              description3 = None,
              description4 = None,
              numberOfBeneficiary = "501",
              identification = Some(IdentificationOrgType(
                None,
                address = Some(
                  AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), GB)
                )
              )),
              beneficiaryDiscretion = None,
              beneficiaryShareOfIncome = None,
              countryOfResidence = None
            )
          )
      }

      "must not be able to create LargeBeneficaryType when incomplete data " in {
        val userAnswers =
          emptyUserAnswers
            .set(LargeBeneficiaryNamePage(index0), "Employment Related Name").success.value

        mapper.build(userAnswers) mustNot be(defined)
      }

    }
  }
}


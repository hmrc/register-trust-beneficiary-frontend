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
import models.core.pages.{InternationalAddress, UKAddress}
import models.{AddressType, OtherType}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5.CountryOfResidenceInTheUkYesNoPage
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.other.mld5._
import utils.Constants.GB

class OtherBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val otherBeneficiaryMapper: OtherBeneficiaryMapper = injector.instanceOf[OtherBeneficiaryMapper]

  private val description: String = "Other Description"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val ukAddressType: AddressType = AddressType("Line 1", "Line 2", None, None, Some("POSTCODE"), GB)
  private val country: String = "FR"
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, country)
  private val internationalAddressType: AddressType = AddressType("Line 1", "Line 2", None, None, None, country)
  private val percentage: Int = 50

  "Other Beneficiary Mapper" when {

    "when user answers is empty" must {

      "must not be able to create a beneficiary of type Other" in {
        val userAnswers = emptyUserAnswers
        otherBeneficiaryMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      val index = 0

      "must be able to create a beneficiary of type Other" when {

        "no country of residence, no address and discretion" in {

          val userAnswers = emptyUserAnswers
            .set(DescriptionPage(index), description).success.value
            .set(IncomeDiscretionYesNoPage(index), true).success.value
            .set(CountryOfResidenceYesNoPage(index), false).success.value
            .set(AddressYesNoPage(index), false).success.value

          val other = otherBeneficiaryMapper.build(userAnswers)

          other mustBe defined
          other.value.head mustBe OtherType(
            description = description,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            address = None,
            countryOfResidence = None
          )
        }

        "for a non-taxable journey" in {

          val userAnswers = emptyUserAnswers
            .set(DescriptionPage(index), description).success.value
            .set(CountryOfResidenceYesNoPage(index), false).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
            .set(CountryOfResidencePage(index), "FR").success.value

          val other = otherBeneficiaryMapper.build(userAnswers)

          other mustBe defined
          other.value.head mustBe OtherType(
            description = description,
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            address = None,
            countryOfResidence = Some("FR")
          )
        }

        "for a taxable journey" when {

          "UK country of residence, UK address and no discretion" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage(index), description).success.value
              .set(IncomeDiscretionYesNoPage(index), false).success.value
              .set(ShareOfIncomePage(index), percentage).success.value
              .set(CountryOfResidenceYesNoPage(index), true).success.value
              .set(UKResidentYesNoPage(index), true).success.value
              .set(AddressYesNoPage(index), true).success.value
              .set(AddressUKYesNoPage(index), true).success.value
              .set(AddressUKPage(index), ukAddress).success.value

            val other = otherBeneficiaryMapper.build(userAnswers)

            other mustBe defined
            other.value.head mustBe OtherType(
              description = description,
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some(percentage.toString),
              address = Some(ukAddressType),
              countryOfResidence = Some(GB)
            )
          }

          "non-UK residence, non-UK address and no discretion" in {

            val userAnswers = emptyUserAnswers
              .set(DescriptionPage(index), description).success.value
              .set(IncomeDiscretionYesNoPage(index), false).success.value
              .set(ShareOfIncomePage(index), percentage).success.value
              .set(CountryOfResidenceYesNoPage(index), true).success.value
              .set(UKResidentYesNoPage(index), false).success.value
              .set(CountryOfResidencePage(index), country).success.value
              .set(AddressYesNoPage(index), true).success.value
              .set(AddressUKYesNoPage(index), false).success.value
              .set(AddressInternationalPage(index), internationalAddress).success.value

            val other = otherBeneficiaryMapper.build(userAnswers)

            other mustBe defined
            other.value.head mustBe OtherType(
              description = description,
              beneficiaryDiscretion = Some(false),
              beneficiaryShareOfIncome = Some(percentage.toString),
              address = Some(internationalAddressType),
              countryOfResidence = Some(country)
            )
          }

        }
      }
    }
  }
}

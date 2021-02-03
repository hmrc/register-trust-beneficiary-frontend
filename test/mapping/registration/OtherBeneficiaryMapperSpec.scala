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
import mapping.Mapping
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.other.mld5.{CountryOfResidencePage, CountryOfResidenceYesNoPage, UKResidentYesNoPage}
import pages.register.beneficiaries.other.{AddressInternationalPage, AddressUKPage, AddressUKYesNoPage, AddressYesNoPage, DescriptionPage, IncomeDiscretionYesNoPage}

class OtherBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val otherBeneficiaryMapper: Mapping[List[OtherType]] = injector.instanceOf[OtherBeneficiaryMapper]

  private val name: String = "Other Name"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "COUNTRY")

  "Other Beneficiary Mapper" when {

    "when user answers is empty" must {

      "must not be able to create a beneficiary of type Other" in {
        val userAnswers = emptyUserAnswers
        otherBeneficiaryMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must be able to create a beneficiary of type Other" when {

        val index = 0

        "no address" in {
          val userAnswers = emptyUserAnswers
            .set(DescriptionPage(index), name).success.value
            .set(IncomeDiscretionYesNoPage(index), true).success.value
            .set(AddressYesNoPage(index), false).success.value

          otherBeneficiaryMapper.build(userAnswers).value.head mustBe OtherType(
            description = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            address = None,
            countryOfResidence = None
          )
        }

        "Country of residence is set to the UK in 5mld mode" in {
          val userAnswers =
            emptyUserAnswers
              .set(DescriptionPage(index), name).success.value
              .set(CountryOfResidenceYesNoPage(index), true).success.value
              .set(UKResidentYesNoPage(index), true).success.value
              .set(AddressYesNoPage(index), true).success.value
              .set(AddressUKYesNoPage(index), true).success.value
              .set(AddressUKPage(index), ukAddress).success.value

          val other = otherBeneficiaryMapper.build(userAnswers)

          other mustBe defined
          other.value.head mustBe OtherType(
            description = "Other Name",
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            address = Some(AddressType("Line 1", "Line 2", None, Some("COUNTRY"), Some("POSTCODE"), "GB")),
            countryOfResidence = None
          )
        }

        "Country of residence is set to outside the UK in 5mld mode" in {
          val userAnswers =
            emptyUserAnswers
              .set(DescriptionPage(index), name).success.value
              .set(CountryOfResidenceYesNoPage(index), true).success.value
              .set(UKResidentYesNoPage(index), false).success.value
              .set(CountryOfResidencePage(index), "FR").success.value
              .set(AddressYesNoPage(index), true).success.value
              .set(AddressUKYesNoPage(index), false).success.value
              .set(AddressInternationalPage(index), internationalAddress).success.value

          val other = otherBeneficiaryMapper.build(userAnswers)

          other mustBe defined
          other.value.head mustBe OtherType(
            description = "Other Name",
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None,
            address = Some(AddressType("Line 1", "Line 2", None, None, None, "FR")),
            countryOfResidence = Some("FR")
          )
        }
      }
    }
  }
}

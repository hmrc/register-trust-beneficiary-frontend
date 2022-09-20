/*
 * Copyright 2022 HM Revenue & Customs
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
import models.{AddressType, CharityType, IdentificationOrgType}
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.charity.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import utils.Constants._

class CharityBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val charityBeneficiaryMapper: CharityBeneficiaryMapper = injector.instanceOf[CharityBeneficiaryMapper]

  private val name: String = "Charity Name"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "COUNTRY")

  "Charity Beneficiary Mapper" when {

    "when user answers is empty" must {

      "must not be able to create a beneficiary of type Charity" in {
        val userAnswers = emptyUserAnswers
        charityBeneficiaryMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must be able to create a beneficiary of type Charity" when {

        val index = 0

        "no address" in {
          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(index), name).right.get
            .set(AmountDiscretionYesNoPage(index), true).right.get
            .set(AddressYesNoPage(index), false).right.get

          charityBeneficiaryMapper.build(userAnswers).value.head mustBe CharityType(
            organisationName = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = None
          )
        }

        "Country of residence is set to the UK" in {
          val userAnswers =
            emptyUserAnswers
              .set(CharityNamePage(index), name).right.get
              .set(AmountDiscretionYesNoPage(index), false).right.get
              .set(HowMuchIncomePage(index),60).right.get
              .set(CountryOfResidenceYesNoPage(index), true).right.get
              .set(CountryOfResidenceInTheUkYesNoPage(index), true).right.get
              .set(AddressYesNoPage(index), false).right.get

          val charities = charityBeneficiaryMapper.build(userAnswers)

          charities mustBe defined
          charities.value.head mustBe CharityType(
            organisationName = "Charity Name",
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("60"),
            identification = None,
            countryOfResidence = Some(GB)
          )
        }

        "Country of residence is set to outside the UK" in {
          val userAnswers =
            emptyUserAnswers
              .set(CharityNamePage(index), "Charity Name").right.get
              .set(AmountDiscretionYesNoPage(index), false).right.get
              .set(HowMuchIncomePage(index), 100).right.get
              .set(CountryOfResidenceYesNoPage(index), true).right.get
              .set(CountryOfResidenceInTheUkYesNoPage(index), false).right.get
              .set(CountryOfResidencePage(index), "FR").right.get
              .set(AddressYesNoPage(index), false).right.get

          val charities = charityBeneficiaryMapper.build(userAnswers)

          charities mustBe defined
          charities.value.head mustBe CharityType(
            organisationName = "Charity Name",
            beneficiaryDiscretion = Some(false),
            beneficiaryShareOfIncome = Some("100"),
            identification = None,
            countryOfResidence = Some("FR")
          )
        }

      "UK address" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(index), name).right.get
            .set(AmountDiscretionYesNoPage(index), true).right.get
            .set(AddressYesNoPage(index), true).right.get
            .set(AddressInTheUkYesNoPage(index), true).right.get
            .set(CharityAddressUKPage(index), ukAddress).right.get

          charityBeneficiaryMapper.build(userAnswers).value.head mustBe CharityType(
            organisationName = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              utr = None,
              address = Some(
                AddressType(ukAddress.line1, ukAddress.line2, ukAddress.line3, ukAddress.line4, Some(ukAddress.postcode), GB)
              )
            )),
            countryOfResidence = None
            )
        }

        "international address" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(index), name).right.get
            .set(AmountDiscretionYesNoPage(index), true).right.get
            .set(AddressYesNoPage(index), true).right.get
            .set(AddressInTheUkYesNoPage(index), false).right.get
            .set(CharityInternationalAddressPage(index), internationalAddress).right.get

          charityBeneficiaryMapper.build(userAnswers).value.head mustBe CharityType(
            organisationName = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              utr = None,
              address = Some(
                AddressType(internationalAddress.line1, internationalAddress.line2, internationalAddress.line3, None, None, internationalAddress.country)
              )
            )),
            countryOfResidence = None
          )
        }
      }

      "must be able to create multiple charity beneficiaries" in {
        val index0 = 0
        val index1 = 1
        val name1 = "Name 1"
        val name2 = "Name 2"

        val userAnswers =
          emptyUserAnswers
            .set(CharityNamePage(index0), name1).right.get
            .set(AmountDiscretionYesNoPage(index0), true).right.get
            .set(AddressYesNoPage(index0), false).right.get

            .set(CharityNamePage(index1), name2).right.get
            .set(AmountDiscretionYesNoPage(index1), true).right.get
            .set(AddressYesNoPage(index1), false).right.get

        charityBeneficiaryMapper.build(userAnswers).value mustBe List(
          CharityType(
            organisationName = name1,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = None
          ),
          CharityType(
            organisationName = name2,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = None,
            countryOfResidence = None
          )
        )
      }
    }
  }
}

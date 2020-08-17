/*
 * Copyright 2020 HM Revenue & Customs
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
import pages.register.beneficiaries.charityortrust.charity._

class CharityBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val charityBeneficiaryMapper: Mapping[List[CharityType]] = injector.instanceOf[CharityBeneficiaryMapper]

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

        "no address" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(index), name).success.value
            .set(AmountDiscretionYesNoPage(index), true).success.value
            .set(AddressYesNoPage(index), false).success.value

          charityBeneficiaryMapper.build(userAnswers).value.head mustBe CharityType(
            organisationName = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = None
          )
        }

        "UK address" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(index), name).success.value
            .set(AmountDiscretionYesNoPage(index), true).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressInTheUkYesNoPage(index), true).success.value
            .set(CharityAddressUKPage(index), ukAddress).success.value

          charityBeneficiaryMapper.build(userAnswers).value.head mustBe CharityType(
            organisationName = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              utr = None,
              address = Some(
                AddressType(ukAddress.line1, ukAddress.line2, ukAddress.line3, ukAddress.line4, Some(ukAddress.postcode), "GB")
              )
            ))
          )
        }

        "international address" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(index), name).success.value
            .set(AmountDiscretionYesNoPage(index), true).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressInTheUkYesNoPage(index), false).success.value
            .set(CharityInternationalAddressPage(index), internationalAddress).success.value

          charityBeneficiaryMapper.build(userAnswers).value.head mustBe CharityType(
            organisationName = name,
            beneficiaryDiscretion = Some(true),
            beneficiaryShareOfIncome = None,
            identification = Some(IdentificationOrgType(
              utr = None,
              address = Some(
                AddressType(internationalAddress.line1, internationalAddress.line2, internationalAddress.line3, None, None, internationalAddress.country)
              )
            ))
          )
        }
      }

//      "must be able to list of 'class of beneficiary' (UnidentifiedType)" in {
//        val index0 = 0
//        val index1 = 1
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(ClassBeneficiaryDescriptionPage(index0), "class of ben 1").success.value
//            .set(ClassBeneficiaryDescriptionPage(index1), "class of ben 2").success.value
//
//        charityBeneficiaryMapper.build(userAnswers).value mustBe List(UnidentifiedType(
//          description = "class of ben 1",
//          beneficiaryDiscretion = None,
//          beneficiaryShareOfIncome = None),
//          UnidentifiedType(
//          description = "class of ben 2",
//          beneficiaryDiscretion = None,
//          beneficiaryShareOfIncome = None)
//        )
//      }
//
//      "must not able to create a beneficiary of 'class of beneficiary' (UnidentifiedType) when only this type of beneficiary does not exist" in {
//
//        val userAnswers =
//          emptyUserAnswers
//            .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).success.value
//
//        charityBeneficiaryMapper.build(userAnswers) mustBe None
//
//      }


    }

  }

}

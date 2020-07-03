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

import java.time.LocalDate

import base.SpecBase
import generators.Generators
import mapping._
import models.core.pages.{FullName, UKAddress}
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import pages.register.beneficiaries.individual._

class IndividualBeneficiaryMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val individualBeneficiariesMapper: Mapping[List[IndividualDetailsType]] = injector.instanceOf[IndividualBeneficiaryMapper]

  "IndividualBeneficiariesMapper" when {

    "when user answers is empty" must {

      "must not be able to create IndividualDetailsType" in {

        val userAnswers = emptyUserAnswers

        individualBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must be able to create IndividualDetailsType with Nino information." in {
        val index = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryDateOfBirthPage(index), dateOfBirth).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryIncomePage(index), "100").success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryNationalInsuranceNumberPage(index), "AB123456C").success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(index), true).success.value

        individualBeneficiariesMapper.build(userAnswers) mustBe defined
        individualBeneficiariesMapper.build(userAnswers).value.head mustBe IndividualDetailsType(
          name = NameType("first name", None, "last name"),
          dateOfBirth = Some(dateOfBirth),
          vulnerableBeneficiary = true,
          beneficiaryType = None,
          beneficiaryDiscretion = false,
          beneficiaryShareOfIncome = Some("100"),
          identification = Some(IdentificationType(nino = Some("AB123456C"), None, None))
        )
      }

      "must be able to create IndividualDetailsType with UK Address" in {
        val index = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryDateOfBirthPage(index), dateOfBirth).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryAddressYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryAddressUKYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryAddressUKPage(index),
              UKAddress("Line1", "Line2", None, Some("Newcastle"), "NE62RT")).success.value


        individualBeneficiariesMapper.build(userAnswers) mustBe defined
        individualBeneficiariesMapper.build(userAnswers).value.head mustBe IndividualDetailsType(
          name = NameType("first name", None, "last name"),
          dateOfBirth = Some(dateOfBirth),
          vulnerableBeneficiary = false,
          beneficiaryType = None,
          beneficiaryDiscretion = true,
          beneficiaryShareOfIncome = None,
          identification = Some(IdentificationType(
            nino = None,
            None,
            address = Some(
              AddressType("Line1", "Line2", None, Some("Newcastle"), Some("NE62RT"), "GB")
            )
          ))
        )
      }

      "must be able to create IndividualDetailsType without Nino And Address" in {
        val index = 0
        val userAnswers =
          emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryAddressYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(index), false).success.value

        individualBeneficiariesMapper.build(userAnswers) mustBe defined
        individualBeneficiariesMapper.build(userAnswers).value.head mustBe IndividualDetailsType(
          name = NameType("first name", None, "last name"),
          dateOfBirth = None,
          vulnerableBeneficiary = false,
          beneficiaryType = None,
          beneficiaryDiscretion = true,
          beneficiaryShareOfIncome = None,
          identification = None
        )
      }

      "must be able to create multiple IndividualDetailsType, first with Nino and second with UKAddress" in {
        val index0 = 0
        val index1 = 1
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers =
          emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(index0), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index0), true).success.value
            .set(IndividualBeneficiaryDateOfBirthPage(index0), dateOfBirth).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index0), false).success.value
            .set(IndividualBeneficiaryIncomePage(index0), "100").success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index0), true).success.value
            .set(IndividualBeneficiaryNationalInsuranceNumberPage(index0), "AB123456C").success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(index0), true).success.value

            .set(IndividualBeneficiaryNamePage(index1), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index1), true).success.value
            .set(IndividualBeneficiaryDateOfBirthPage(index1), dateOfBirth).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index1), false).success.value
            .set(IndividualBeneficiaryIncomePage(index1), "100").success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index1), false).success.value
            .set(IndividualBeneficiaryVulnerableYesNoPage(index1), false).success.value
            .set(IndividualBeneficiaryAddressYesNoPage(index1), true).success.value
            .set(IndividualBeneficiaryAddressUKYesNoPage(index1), true).success.value
            .set(IndividualBeneficiaryAddressUKPage(index1),
              UKAddress("line1", "line2", None, None, "NE62RT")).success.value

        individualBeneficiariesMapper.build(userAnswers) mustBe defined
        individualBeneficiariesMapper.build(userAnswers).value mustBe
          List(
            IndividualDetailsType(
              name = NameType("first name", None, "last name"),
              dateOfBirth = Some(dateOfBirth),
              vulnerableBeneficiary = true,
              beneficiaryType = None,
              beneficiaryDiscretion = false,
              beneficiaryShareOfIncome = Some("100"),
              identification = Some(
                IdentificationType(
                  nino = Some("AB123456C"),
                  passport = None,
                  address = None)
              )),

            IndividualDetailsType(
              name = NameType("first name", None, "last name"),
              dateOfBirth = Some(dateOfBirth),
              vulnerableBeneficiary = false,
              beneficiaryType = None,
              beneficiaryDiscretion = false,
              beneficiaryShareOfIncome = Some("100"),
              identification = Some(
                IdentificationType(
                  nino = None,
                  passport = None,
                  address = Some(
                    AddressType("line1", "line2", None, None, Some("NE62RT"), "GB")
                  ))
              ))
          )
      }

      "must not be able to create IndividualDetailsType when incomplete data " in {
        val index = 0
        val userAnswers =
          emptyUserAnswers
            .set(IndividualBeneficiaryNamePage(index), FullName("first name", None, "last name")).success.value
            .set(IndividualBeneficiaryDateOfBirthYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryIncomeYesNoPage(index), true).success.value
            .set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), false).success.value
            .set(IndividualBeneficiaryAddressYesNoPage(index), false).success.value

        individualBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }
  }


}

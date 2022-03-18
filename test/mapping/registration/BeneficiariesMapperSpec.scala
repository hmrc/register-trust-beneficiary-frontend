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
import models.core.pages.{Description, FullName}
import models.registration.pages.HowManyBeneficiaries
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.charityortrust.{charity, trust}
import pages.register.beneficiaries.companyoremploymentrelated.{company, employmentRelated}
import pages.register.beneficiaries.{classofbeneficiaries, individual, other}

import java.time.LocalDate

class BeneficiariesMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val beneficiariesMapper: BeneficiariesMapper = injector.instanceOf[BeneficiariesMapper]

  "BeneficiariesMapper" when {

    "when user answers is empty" must {

      "must not be able to create BeneficiaryType" in {

        val userAnswers = emptyUserAnswers

        beneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must not be able to create BeneficiaryType when there is incomplete data" in {
        beneficiariesMapper.build(emptyUserAnswers) mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is an individual beneficiary" in {

        val index = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers = emptyUserAnswers
          .set(individual.NamePage(index), FullName("first name", None, "last name")).success.value
          .set(individual.DateOfBirthYesNoPage(index), true).success.value
          .set(individual.DateOfBirthPage(index), dateOfBirth).success.value
          .set(individual.IncomeYesNoPage(index), false).success.value
          .set(individual.IncomePage(index), 100).success.value
          .set(individual.NationalInsuranceYesNoPage(index), true).success.value
          .set(individual.NationalInsuranceNumberPage(index), "AB123456C").success.value
          .set(individual.VulnerableYesNoPage(index), true).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustBe defined
        result.unidentified mustNot be(defined)
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is an individual beneficiary and class of beneficiary" in {

        val index = 0
        val classOfBeneficiaryIndex = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers = emptyUserAnswers
          .set(individual.NamePage(index), FullName("first name", None, "last name")).success.value
          .set(individual.DateOfBirthYesNoPage(index), true).success.value
          .set(individual.DateOfBirthPage(index), dateOfBirth).success.value
          .set(individual.IncomeYesNoPage(index), false).success.value
          .set(individual.IncomePage(index), 100).success.value
          .set(individual.NationalInsuranceYesNoPage(index), true).success.value
          .set(individual.NationalInsuranceNumberPage(index), "AB123456C").success.value
          .set(individual.VulnerableYesNoPage(index), true).success.value
          .set(classofbeneficiaries.ClassBeneficiaryDescriptionPage(classOfBeneficiaryIndex), "class of ben 1").success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustBe defined
        result.unidentified mustBe defined
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is an individual beneficiary , class of beneficiary and charity beneficiary" in {

        val index = 0
        val classOfBeneficiaryIndex = 0
        val charityBeneficiaryIndex = 0
        val dateOfBirth = LocalDate.of(2010, 10, 10)

        val userAnswers = emptyUserAnswers
          .set(individual.NamePage(index), FullName("first name", None, "last name")).success.value
          .set(individual.DateOfBirthYesNoPage(index), true).success.value
          .set(individual.DateOfBirthPage(index), dateOfBirth).success.value
          .set(individual.IncomeYesNoPage(index), false).success.value
          .set(individual.IncomePage(index), 100).success.value
          .set(individual.NationalInsuranceYesNoPage(index), true).success.value
          .set(individual.NationalInsuranceNumberPage(index), "AB123456C").success.value
          .set(individual.VulnerableYesNoPage(index), true).success.value
          .set(classofbeneficiaries.ClassBeneficiaryDescriptionPage(classOfBeneficiaryIndex), "class of ben 1").success.value
          .set(charity.CharityNamePage(charityBeneficiaryIndex), "Test").success.value
          .set(charity.AmountDiscretionYesNoPage(charityBeneficiaryIndex), false).success.value
          .set(charity.HowMuchIncomePage(charityBeneficiaryIndex), 100).success.value
          .set(charity.AddressYesNoPage(charityBeneficiaryIndex), false).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustBe defined
        result.unidentified mustBe defined
        result.charity mustBe defined
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is a company beneficiary" in {

        val index = 0

        val userAnswers = emptyUserAnswers
          .set(company.NamePage(index), "Company Name").success.value
          .set(company.IncomeYesNoPage(index), false).success.value
          .set(company.IncomePage(index), 100).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustNot be(defined)
        result.unidentified mustNot be(defined)
        result.charity mustNot be(defined)
        result.company must be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is a large beneficiary" in {

        val index = 0

        val userAnswers = emptyUserAnswers
          .set(employmentRelated.LargeBeneficiaryNamePage(index), "Employment Related Name").success.value
          .set(employmentRelated.LargeBeneficiaryAddressYesNoPage(index), false).success.value
          .set(employmentRelated.LargeBeneficiaryDescriptionPage(index), Description("Description", None, None, None, None)).success.value
          .set(employmentRelated.LargeBeneficiaryNumberOfBeneficiariesPage(index), HowManyBeneficiaries.Over1).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustNot be(defined)
        result.unidentified mustNot be(defined)
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large must be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is a trust beneficiary" in {

        val index = 0

        val userAnswers = emptyUserAnswers
          .set(trust.NamePage(index), "Trust Name").success.value
          .set(trust.DiscretionYesNoPage(index), false).success.value
          .set(trust.ShareOfIncomePage(index), 100).success.value
          .set(trust.AddressYesNoPage(index), false).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustNot be(defined)
        result.unidentified mustNot be(defined)
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust must be(defined)
        result.large mustNot be(defined)
        result.other mustNot be(defined)
      }

      "must be able to create BeneficiaryType when there is an other beneficiary" in {

        val index = 0

        val userAnswers = emptyUserAnswers
          .set(other.DescriptionPage(index), "Other Description").success.value
          .set(other.IncomeDiscretionYesNoPage(index), false).success.value
          .set(other.ShareOfIncomePage(index), 100).success.value

        val result = beneficiariesMapper.build(userAnswers).value

        result.individualDetails mustNot be(defined)
        result.unidentified mustNot be(defined)
        result.charity mustNot be(defined)
        result.company mustNot be(defined)
        result.trust mustNot be(defined)
        result.large mustNot be(defined)
        result.other must be(defined)
      }

    }
  }
}

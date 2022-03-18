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

package navigation

import base.SpecBase
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.{routes => mld5Rts}
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => rts}
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}

class EmploymentRelatedBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new EmploymentRelatedBeneficiaryNavigator
  val index = 0

  "Employment related beneficiary navigator" must {

    "go to AddressUkYesNo from AddressYesNo if Yes" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(LargeBeneficiaryAddressYesNoPage(index), true).success.value
          navigator.nextPage(LargeBeneficiaryAddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(rts.AddressUkYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to Description from AddressYesNo if No" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(LargeBeneficiaryAddressYesNoPage(index), false).success.value
          navigator.nextPage(LargeBeneficiaryAddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to UkAddress from AddressYesUkNo if Yes" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(LargeBeneficiaryAddressUKYesNoPage(index), true).success.value
          navigator.nextPage(LargeBeneficiaryAddressUKYesNoPage(index), fakeDraftId, answers)
            .mustBe(rts.UkAddressController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to NonUkAddress from AddressYesUkNo if No" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(LargeBeneficiaryAddressUKYesNoPage(index), false).success.value
          navigator.nextPage(LargeBeneficiaryAddressUKYesNoPage(index), fakeDraftId, answers)
            .mustBe(rts.NonUkAddressController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to Description from UkAddress " in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(LargeBeneficiaryAddressPage(index), fakeDraftId, userAnswers)
            .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to Description from NonUkAddress " in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(LargeBeneficiaryAddressInternationalPage(index), fakeDraftId, userAnswers)
            .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to NumberOfBeneficiaries from Description" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(LargeBeneficiaryDescriptionPage(index), fakeDraftId, userAnswers)
            .mustBe(rts.NumberOfBeneficiariesController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to CheckAnswers from NumberOfBeneficiaries" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(LargeBeneficiaryNumberOfBeneficiariesPage(index), fakeDraftId, userAnswers)
            .mustBe(rts.CheckDetailsController.onPageLoad(index, fakeDraftId))
      }
    }

    "a taxable trust" must {
      val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

      "Name page -> CountryOfResidence Yes No page" in {
        navigator.nextPage(LargeBeneficiaryNamePage(index), draftId, baseAnswers)
          .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> No -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
      }


      "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "CountryOfResidence page -> Address yes no page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
          .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
      }
    }

    "a non taxable trust" must {
      val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

      "Name page -> CountryOfResidence Yes No page" in {
        navigator.nextPage(LargeBeneficiaryNamePage(index), draftId, baseAnswers)
          .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> No -> Description page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
      }


      "CountryOfResidence Uk yes no page -> Yes -> Description page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "CountryOfResidence page -> Description page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
          .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
      }
    }
  }
}

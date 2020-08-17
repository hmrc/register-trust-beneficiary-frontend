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

package navigation

import base.SpecBase
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => rts}
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._

class EmploymentRelatedBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new EmploymentRelatedBeneficiaryNavigator
  val index = 0

  "Employment related beneficiary navigator" must {

    "go to AddressYesNo from NamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(LargeBeneficiaryNamePage(index), fakeDraftId, userAnswers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

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


  }
}

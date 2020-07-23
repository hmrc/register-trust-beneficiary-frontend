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
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => CompanyRoutes}
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.company._

class CompanyBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new CompanyBeneficiaryNavigator
  val index = 0

  "Company beneficiary navigator" must {

    "go to DiscretionYesNo from NamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(NamePage(index), fakeDraftId, userAnswers)
            .mustBe(CompanyRoutes.DiscretionYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to AddressYesNo from DiscretionYesNo if Yes" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(DiscretionYesNoPage(index), true).success.value
          navigator.nextPage(DiscretionYesNoPage(index), fakeDraftId, answers)
            .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to ShareOfIncomePage from DiscretionYesNo if No" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(DiscretionYesNoPage(index), false).success.value
          navigator.nextPage(DiscretionYesNoPage(index), fakeDraftId, answers)
            .mustBe(CompanyRoutes.ShareOfIncomeController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to AddressYesNo from ShareOfIncomePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(ShareOfIncomePage(index), fakeDraftId, userAnswers)
            .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to AddressUkYesNo from AddressYesNo if Yes" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(AddressYesNoPage(index), true).success.value
          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(CompanyRoutes.AddressUkYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to CheckDetails from AddressYesNo if No" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(AddressYesNoPage(index), false).success.value
          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(CompanyRoutes.CheckDetailsController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to UkAddress from AddressYesUkNo if Yes" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(AddressUKYesNoPage(index), true).success.value
          navigator.nextPage(AddressUKYesNoPage(index), fakeDraftId, answers)
            .mustBe(CompanyRoutes.UkAddressController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to NonUkAddress from AddressYesUkNo if No" in {
      forAll(arbitrary[UserAnswers]) {
        baseAnswers =>
          val answers = baseAnswers.set(AddressUKYesNoPage(index), false).success.value
          navigator.nextPage(AddressUKYesNoPage(index), fakeDraftId, answers)
            .mustBe(CompanyRoutes.NonUkAddressController.onPageLoad(index, fakeDraftId))
      }
    }
    "go to CheckDetails from UkAddress " in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AddressUKPage(index), fakeDraftId, userAnswers)
            .mustBe(CompanyRoutes.CheckDetailsController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to CheckDetails from NonUkAddress " in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AddressInternationalPage(index), fakeDraftId, userAnswers)
            .mustBe(CompanyRoutes.CheckDetailsController.onPageLoad(index, fakeDraftId))
      }
    }
  }
}

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
import config.FrontendAppConfig
import controllers.register.beneficiaries.classofbeneficiaries.{routes => classOfBeneficiariesRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import controllers.register.beneficiaries.routes
import generators.Generators
import models.core.pages.FullName
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries._
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}

class BeneficiaryNavigatorSpec {
  self: ScalaCheckPropertyChecks with Generators with SpecBase =>

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  val navigator: BeneficiaryNavigator = injector.instanceOf[BeneficiaryNavigator]

  "go to WhatTypeOfBeneficiaryPage from AddABeneficiaryYesNoPage when selected yes" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddABeneficiaryYesNoPage, true).success.value

        navigator.nextPage(AddABeneficiaryYesNoPage, NormalMode, fakeDraftId, answers)
          .mustBe(routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
    }
  }

  "go to WhatTypeOfBeneficiaryPage from AddABeneficiaryPage when selected add them now" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesNow).success.value

        navigator.nextPage(AddABeneficiaryPage, NormalMode, fakeDraftId, answers)
          .mustBe(routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
    }
  }

  "go to RegistrationProgress from AddABeneficiaryYesNoPage when selected no" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddABeneficiaryYesNoPage, false).success.value

        navigator.nextPage(AddABeneficiaryYesNoPage, NormalMode, fakeDraftId, answers)
          .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
    }
  }

  "go to RegistrationProgress from AddABeneficiaryPage" when {
    "selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.YesLater).success.value

          navigator.nextPage(AddABeneficiaryPage, NormalMode, fakeDraftId, answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

          navigator.nextPage(AddABeneficiaryPage, NormalMode, fakeDraftId, answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }
  }

  "go to feature not available when beneficiary option selected that is not available" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>
        val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CompanyOrEmployment).success.value
        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
          .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
    }
  }

  "Individual Beneficiary" when {

    "there are no Individual Beneficiaries" must {
      "go to IndividualBeneficiaryNamePage for index 0 from WhatTypeOfBeneficiaryPage when Individual option selected " in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value
              .remove(IndividualBeneficiaries).success.value
            navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
              .mustBe(individualRoutes.NameController.onPageLoad(0, fakeDraftId))
        }
      }
    }

    "there is at least one Individual Beneficiary" must {
      "go to the next IndividualBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when Individual option selected" in {

        val answers = emptyUserAnswers
          .set(NamePage(0), FullName("First", None, "Last")).success.value
          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value

        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
          .mustBe(individualRoutes.NameController.onPageLoad(1, fakeDraftId))
      }
    }
  }

  "Class of Beneficiaries" when {

    "there are no Class of Beneficiaries" must {
      "go to ClassBeneficiaryDescriptionPage for index 0 from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected " in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
              .remove(ClassOfBeneficiaries).success.value
            navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
              .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(0, fakeDraftId))
        }
      }
    }

    "there is at least one Class of beneficiary" must {
      "go to the next ClassBeneficiaryDescriptionPage from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected" in {

        val answers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value

        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
          .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(1, fakeDraftId))
      }
    }
  }

}

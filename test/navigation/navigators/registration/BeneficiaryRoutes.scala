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

package navigation.navigators.registration

import base.SpecBase
import config.FrontendAppConfig
import controllers.register.beneficiaries.routes
import generators.Generators
import models.{NormalMode, UserAnswers}
import models.core.pages.FullName
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage}
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}

trait BeneficiaryRoutes {
  self: ScalaCheckPropertyChecks with Generators with SpecBase =>

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig) : Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  def beneficiaryRoutes()(implicit navigator: Navigator): Unit = {

      "go to WhatTypeOfBeneficiaryPage from AddABeneficiaryYesNoPage when selected yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddABeneficiaryYesNoPage, true).success.value

            navigator.nextPage(AddABeneficiaryYesNoPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
        }
      }

    "go to RegistrationProgress from AddABeneficiaryYesNoPage when selected no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddABeneficiaryYesNoPage, false).success.value

          navigator.nextPage(AddABeneficiaryYesNoPage, NormalMode, fakeDraftId)(answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

      "go WhatTypeOfBeneficiaryPage from AddABeneficiaryPage when selected add them now" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesNow).success.value

            navigator.nextPage(AddABeneficiaryPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
        }
      }

    "go to RegistrationProgress from AddABeneficiaryPage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.YesLater).success.value

          navigator.nextPage(AddABeneficiaryPage, NormalMode, fakeDraftId)(answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "go to RegistrationProgress from AddABeneficiaryPage when selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

          navigator.nextPage(AddABeneficiaryPage, NormalMode, fakeDraftId)(answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    val indexForBeneficiary = 0

    "go to IndividualBeneficiaryDateOfBirthYesNoPage from IndividualBeneficiaryNamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryNamePage(indexForBeneficiary), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryDateOfBirthPage from IndividualBeneficiaryDateOfBirthYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryIncomeYesNoPage from IndividualBeneficiaryDateOfBirthYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryDateOfBirthYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryIncomeYesNoPage from IndividualBeneficiaryDateOfBirthPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryDateOfBirthPage(indexForBeneficiary), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryIncomePage from IndividualBeneficiaryIncomeYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceYesNoPage from IndividualBeneficiaryIncomeYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryIncomeYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceYesNoPage from IndividualBeneficiaryIncomePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryIncomePage(indexForBeneficiary), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryAddressYesNoPage from IndividualBeneficiaryNationalInsuranceYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryNationalInsuranceNumberPage from IndividualBeneficiaryNationalInsuranceYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryVulnerableYesNoPage from IndividualBeneficiaryNationalInsuranceNumberPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryNationalInsuranceNumberPage(indexForBeneficiary), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryAddressUKYesNoPage from IndividualBeneficiaryAddressYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryVulnerableYesNoPage from IndividualBeneficiaryAddressYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryAddressYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryAddressUKPage from IndividualBeneficiaryAddressUKYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryAddressUKYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IndividualBeneficiaryAddressUKYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryAddressUKYesNoPage from IndividualBeneficiaryAddressUKPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualBeneficiaryAddressUKYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IndividualBeneficiaryAddressUKYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(answers)
            .mustBe(routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryVulnerableYesNoPage from IndividualBeneficiaryAddressUKPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryAddressUKPage(indexForBeneficiary), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IndividualBeneficiaryAnswersPage from IndividualBeneficiaryVulnerableYesNoPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryVulnerableYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.IndividualBeneficiaryAnswersController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AddABeneficiaryPage from IndividualBeneficiaryAnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IndividualBeneficiaryAnswersPage, NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
      }
    }

    "there are no Individual Beneficiaries" must {

      "go to IndividualBeneficiaryNamePage for index 0 from WhatTypeOfBeneficiaryPage when Individual option selected " in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value
              .remove(IndividualBeneficiaries).success.value
            navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 0, fakeDraftId))
        }
      }

    }

    "go to feature not available when beneficiary option selected that is not available" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CompanyOrEmployment).success.value
          navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
      }
    }

    "there is atleast one Individual Beneficiaries" must {

      "go to the next IndividualBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when Individual option selected" in {

        val answers = emptyUserAnswers
          .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value

        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
          .mustBe(routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 1, fakeDraftId))
      }
    }

    "there are no Class of Beneficiaries" must {
      "go to ClassBeneficiaryDescriptionPage for index 0 from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected " in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
              .remove(ClassOfBeneficiaries).success.value
            navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
              .mustBe(routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 0, fakeDraftId))
        }
      }
    }



    "there is atleast one Class of beneficiary" must {
      "go to the next ClassBeneficiaryDescriptionPage from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected" in {

        val answers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value

        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
          .mustBe(routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 1, fakeDraftId))
      }
    }

    "go to AddABeneficiaryPage from ClassBeneficiaryDescriptionPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(ClassBeneficiaryDescriptionPage(0), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
      }
    }
  }

}

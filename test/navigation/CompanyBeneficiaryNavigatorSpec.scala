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
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.company.NamePage
import play.api.mvc.Call
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => CompanyRoutes}

class CompanyBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  val navigator = new CompanyBeneficiaryNavigator(frontendAppConfig)
  val index = 0

  "Other beneficiary navigator" when {

    "go to Discretion from company NamePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(NamePage(index), NormalMode, fakeDraftId)(userAnswers)
            .mustBe(CompanyRoutes.DiscretionYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
      }
    }

    //    "go to DateOfBirthYesNoPage from yRoleInCompany" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(RoleInCompanyPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to DateOfBirthYesNoPage from NamePage when kind of trust is missing" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(NamePage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to DateOfBirthPage from DateOfBirthYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(DateOfBirthYesNoPage(index), value = true).success.value
    //          navigator.nextPage(DateOfBirthYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.DateOfBirthController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to IncomeYesNoPage from DateOfBirthYesNoPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(DateOfBirthYesNoPage(index), value = false).success.value
    //          navigator.nextPage(DateOfBirthYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.IncomeYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to IncomeYesNoPage from DateOfBirthPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(DateOfBirthPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.IncomeYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to IncomePage from IncomeYesNoPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(IncomeYesNoPage(index), value = false).success.value
    //          navigator.nextPage(IncomeYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.IncomeController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to NationalInsuranceYesNoPage from IncomeYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(IncomeYesNoPage(index), value = true).success.value
    //          navigator.nextPage(IncomeYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.NationalInsuranceYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to NationalInsuranceYesNoPage from IncomePage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(IncomePage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.NationalInsuranceYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AddressYesNoPage from NationalInsuranceYesNoPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(NationalInsuranceYesNoPage(index), value = false).success.value
    //          navigator.nextPage(NationalInsuranceYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.AddressYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to NationalInsuranceNumberPage from NationalInsuranceYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(NationalInsuranceYesNoPage(index), value = true).success.value
    //          navigator.nextPage(NationalInsuranceYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.NationalInsuranceNumberController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to VulnerableYesNoPage from NationalInsuranceNumberPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(NationalInsuranceNumberPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AddressUKYesNoPage from AddressYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(AddressYesNoPage(index), value = true).success.value
    //          navigator.nextPage(AddressYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.AddressUKYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to VulnerableYesNoPage from AddressYesNoPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(AddressYesNoPage(index), value = false).success.value
    //          navigator.nextPage(AddressYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AddressUKPage from AddressUKYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(AddressUKYesNoPage(index), value = true).success.value
    //          navigator.nextPage(AddressUKYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.AddressUKController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AddressInternationalPage from AddressUKPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(AddressUKYesNoPage(index), value = false).success.value
    //          navigator.nextPage(AddressUKYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.AddressInternationalController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to PassportDetailsYesNoPage from AddressUKPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(AddressUKPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.PassportDetailsYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to PassportDetailsYesNoPage from AddressInternationalPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(AddressInternationalPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.PassportDetailsYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to PassportDetailsPage from PassportDetailsYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(PassportDetailsYesNoPage(index), value = true).success.value
    //          navigator.nextPage(PassportDetailsYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.PassportDetailsController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to VulnerableYesNoPage from PassportDetailsPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(PassportDetailsPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to IdCardDetailsYesNoPage from PassportDetailsYesNoPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(PassportDetailsYesNoPage(index), value = false).success.value
    //          navigator.nextPage(PassportDetailsYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.IDCardDetailsYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to IdCardDetailsPage from IdCardDetailsYesNoPage when user answers yes" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(IDCardDetailsYesNoPage(index), value = true).success.value
    //          navigator.nextPage(IDCardDetailsYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.IDCardDetailsController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to VulnerableYesNoPage from IdCardDetailsYesNoPage when user answers no" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(IDCardDetailsYesNoPage(index), value = false).success.value
    //          navigator.nextPage(IDCardDetailsYesNoPage(index), NormalMode, fakeDraftId)(answers)
    //            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to VulnerableYesNoPage from IdCardDetailsPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(IDCardDetailsPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AnswersPage from VulnerableYesNoPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(VulnerableYesNoPage(index), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(individualRoutes.AnswersController.onPageLoad(index, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AddABeneficiaryPage from AnswersPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(AnswersPage, NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
    //      }
    //    }
    //
    //    "there are no Individual Beneficiaries" must {
    //
    //      "go to IndividualBeneficiaryNamePage for index 0 from WhatTypeOfBeneficiaryPage when Individual option selected " in {
    //        forAll(arbitrary[UserAnswers]) {
    //          userAnswers =>
    //            val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value
    //              .remove(IndividualBeneficiaries).success.value
    //            navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
    //              .mustBe(individualRoutes.NameController.onPageLoad(NormalMode, 0, fakeDraftId))
    //        }
    //      }
    //
    //    }
    //
    //    "go to feature not available when beneficiary option selected that is not available" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CompanyOrEmployment).success.value
    //          navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
    //            .mustBe(controllers.routes.FeatureNotAvailableController.onPageLoad())
    //      }
    //    }
    //
    //    "there is atleast one Individual Beneficiaries" must {
    //
    //      "go to the next IndividualBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when Individual option selected" in {
    //
    //        val answers = emptyUserAnswers
    //          .set(NamePage(0), FullName("First", None, "Last")).success.value
    //          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value
    //
    //        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
    //          .mustBe(individualRoutes.NameController.onPageLoad(NormalMode, 1, fakeDraftId))
    //      }
    //    }
    //
    //    "there are no Class of Beneficiaries" must {
    //      "go to ClassBeneficiaryDescriptionPage for index 0 from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected " in {
    //        forAll(arbitrary[UserAnswers]) {
    //          userAnswers =>
    //            val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
    //              .remove(ClassOfBeneficiaries).success.value
    //            navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
    //              .mustBe(routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 0, fakeDraftId))
    //        }
    //      }
    //    }
    //
    //    "there is atleast one Class of beneficiary" must {
    //      "go to the next ClassBeneficiaryDescriptionPage from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected" in {
    //
    //        val answers = emptyUserAnswers
    //          .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
    //          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
    //
    //        navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId)(answers)
    //          .mustBe(routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 1, fakeDraftId))
    //      }
    //    }
    //
    //    "go to AddABeneficiaryPage from ClassBeneficiaryDescriptionPage" in {
    //      forAll(arbitrary[UserAnswers]) {
    //        userAnswers =>
    //          navigator.nextPage(ClassBeneficiaryDescriptionPage(0), NormalMode, fakeDraftId)(userAnswers)
    //            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
    //      }
    //    }
    //  }
    //
  }
}

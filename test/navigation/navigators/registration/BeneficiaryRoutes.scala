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
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import controllers.register.beneficiaries.routes
import generators.Generators
import models.core.pages.FullName
import models.registration.pages.KindOfTrust.Employees
import models.registration.pages.{AddABeneficiary, CharityOrTrust, WhatTypeOfBeneficiary}
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.KindOfTrustPage
import pages.register.beneficiaries._
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}

trait BeneficiaryRoutes {
  self: ScalaCheckPropertyChecks with Generators with SpecBase =>

  def beneficiaryRoutes()(implicit navigator: Navigator): Unit = {

    "go to CharityOrTrust from WhatTypeOfBeneficiaryPage when CharityOrTrust option selected " in {

      val answers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CharityOrTrust).success.value
      navigator.nextPage(WhatTypeOfBeneficiaryPage, NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.routes.CharityOrTrustController.onPageLoad(NormalMode, fakeDraftId))
    }

    "go to CharityName for index 0 from CharityOrTrust when Charity option selected " in {
      val answers = emptyUserAnswers.set(pages.register.beneficiaries.charityortrust.CharityOrTrustPage, value = CharityOrTrust.Charity).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.CharityOrTrustPage, NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityNameController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to AmountDiscretionYesNo for index 0 from CharityName" in {
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.CharityNamePage(0), NormalMode, fakeDraftId, emptyUserAnswers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.AmountDiscretionYesNoController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to HowMuchIncome for index 0 from AmountDiscretionYesNo when No is selected " in {
      val index = 0
      val answers = emptyUserAnswers
        .set(pages.register.beneficiaries.charityortrust.charity.AmountDiscretionYesNoPage(index), false).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.AmountDiscretionYesNoPage(index), NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.HowMuchIncomeController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to AddressYesNo for index 0 from AmountDiscretionYesNo when yes is selected " in {
      val index = 0
      val answers = emptyUserAnswers
        .set(pages.register.beneficiaries.charityortrust.charity.AmountDiscretionYesNoPage(index), true).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.AmountDiscretionYesNoPage(index), NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.AddressYesNoController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to AddressInTheUkYesNo for index 0 from AddressYesNo when Yes option selected " in {
      val index = 0
      val answers = emptyUserAnswers
        .set(pages.register.beneficiaries.charityortrust.charity.AddressYesNoPage(index), true).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.AddressYesNoPage(index), NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.AddressInTheUkYesNoController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to CharityAnswers for index 0 from AddressYesNo when No option selected " in {
      val index = 0
      val answers = emptyUserAnswers
        .set(pages.register.beneficiaries.charityortrust.charity.AddressYesNoPage(index), false).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.AddressYesNoPage(index), NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityAnswersController.onPageLoad(index = 0, fakeDraftId))
    }

    "go to CharityAddressUK for index 0 from AddressUkYesNo when Yes option selected " in {
      val index = 0
      val answers = emptyUserAnswers
        .set(pages.register.beneficiaries.charityortrust.charity.AddressInTheUkYesNoPage(index), true).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.AddressInTheUkYesNoPage(index), NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityAddressUKController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to CharityAddressInternational for index 0 from AddressUkYesNo when no option selected " in {
      val index = 0
      val answers = emptyUserAnswers
        .set(pages.register.beneficiaries.charityortrust.charity.AddressInTheUkYesNoPage(index), false).success.value
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.AddressInTheUkYesNoPage(index), NormalMode, fakeDraftId, answers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityInternationalAddressController.onPageLoad(NormalMode, 0, fakeDraftId))
    }

    "go to CharityAnswers for index 0 from CharityAddressUk when option selected " in {
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.CharityAddressUKPage(0), NormalMode, fakeDraftId, emptyUserAnswers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityAnswersController.onPageLoad(index = 0, fakeDraftId))
    }

    "go to CharityAnswers for index 0 from CharityAddressInternational when option selected " in {
      navigator.nextPage(pages.register.beneficiaries.charityortrust.charity.CharityInternationalAddressPage(0), NormalMode, fakeDraftId, emptyUserAnswers)
        .mustBe(controllers.register.beneficiaries.charityortrust.charity.routes.CharityAnswersController.onPageLoad(index = 0, fakeDraftId))
    }

    val indexForBeneficiary = 0

    "go to RoleInCompany from NamePage when kind of trust is Employees" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(KindOfTrustPage, Employees).success.value

          navigator.nextPage(NamePage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.RoleInCompanyController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to DateOfBirthYesNoPage from yRoleInCompany" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(RoleInCompanyPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.DateOfBirthYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to DateOfBirthYesNoPage from NamePage when kind of trust is missing" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(NamePage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.DateOfBirthYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to DateOfBirthPage from DateOfBirthYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(DateOfBirthYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(DateOfBirthYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.DateOfBirthController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IncomeYesNoPage from DateOfBirthYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(DateOfBirthYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(DateOfBirthYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.IncomeYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IncomeYesNoPage from DateOfBirthPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(DateOfBirthPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.IncomeYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IncomePage from IncomeYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IncomeYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IncomeYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.IncomeController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to NationalInsuranceYesNoPage from IncomeYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IncomeYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IncomeYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.NationalInsuranceYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to NationalInsuranceYesNoPage from IncomePage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IncomePage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.NationalInsuranceYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AddressYesNoPage from NationalInsuranceYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(NationalInsuranceYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(NationalInsuranceYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.AddressYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to NationalInsuranceNumberPage from NationalInsuranceYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(NationalInsuranceYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(NationalInsuranceYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.NationalInsuranceNumberController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to VulnerableYesNoPage from NationalInsuranceNumberPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(NationalInsuranceNumberPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AddressUKYesNoPage from AddressYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(individual.AddressYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(individual.AddressYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.AddressUKYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to VulnerableYesNoPage from AddressYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(individual.AddressYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(individual.AddressYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AddressUKPage from AddressUKYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(AddressUKYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(AddressUKYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.AddressUKController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AddressInternationalPage from AddressUKPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(AddressUKYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(AddressUKYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.AddressInternationalController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to PassportDetailsYesNoPage from AddressUKPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AddressUKPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.PassportDetailsYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to PassportDetailsYesNoPage from AddressInternationalPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AddressInternationalPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.PassportDetailsYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to PassportDetailsPage from PassportDetailsYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(PassportDetailsYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(PassportDetailsYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.PassportDetailsController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to VulnerableYesNoPage from PassportDetailsPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(PassportDetailsPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IdCardDetailsYesNoPage from PassportDetailsYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(PassportDetailsYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(PassportDetailsYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.IDCardDetailsYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to IdCardDetailsPage from IdCardDetailsYesNoPage when user answers yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IDCardDetailsYesNoPage(indexForBeneficiary), value = true).success.value
          navigator.nextPage(IDCardDetailsYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.IDCardDetailsController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to VulnerableYesNoPage from IdCardDetailsYesNoPage when user answers no" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IDCardDetailsYesNoPage(indexForBeneficiary), value = false).success.value
          navigator.nextPage(IDCardDetailsYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, answers)
            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to VulnerableYesNoPage from IdCardDetailsPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(IDCardDetailsPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.VulnerableYesNoController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AnswersPage from VulnerableYesNoPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(VulnerableYesNoPage(indexForBeneficiary), NormalMode, fakeDraftId, userAnswers)
            .mustBe(individualRoutes.AnswersController.onPageLoad(indexForBeneficiary, fakeDraftId))
      }
    }

    "go to AddABeneficiaryPage from AnswersPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(AnswersPage, NormalMode, fakeDraftId, userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
      }
    }
    
    "go to AddABeneficiaryPage from ClassBeneficiaryDescriptionPage" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          navigator.nextPage(ClassBeneficiaryDescriptionPage(0), NormalMode, fakeDraftId, userAnswers)
            .mustBe(routes.AddABeneficiaryController.onPageLoad(fakeDraftId))
      }
    }
  }

}

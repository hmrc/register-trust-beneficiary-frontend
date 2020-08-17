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
import controllers.register.beneficiaries.charityortrust.charity.{routes => charityRoutes}
import controllers.register.beneficiaries.charityortrust.trust.{routes => trustRoutes}
import controllers.register.beneficiaries.charityortrust.{routes => charityortrustRoutes}
import controllers.register.beneficiaries.classofbeneficiaries.{routes => classOfBeneficiariesRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => companyRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => largeRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.{routes => companyOrEmploymentRelatedRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import controllers.register.beneficiaries.other.{routes => otherRoutes}
import generators.Generators
import models.Status.InProgress
import models.core.pages.FullName
import models.registration.pages.{AddABeneficiary, CharityOrTrust, WhatTypeOfBeneficiary}
import models.{CompanyOrEmploymentRelatedToAdd, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.entitystatus.{CharityBeneficiaryStatus, CompanyBeneficiaryStatus, LargeBeneficiaryStatus, TrustBeneficiaryStatus}
import pages.register.beneficiaries._
import pages.register.beneficiaries.charityortrust.charity.CharityNamePage
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, trust}
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.LargeBeneficiaryNamePage
import pages.register.beneficiaries.companyoremploymentrelated.{CompanyOrEmploymentRelatedPage, company}
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.other.DescriptionPage
import play.api.mvc.Call
import sections.beneficiaries._

class BeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  val navigator: BeneficiaryNavigator = injector.instanceOf[BeneficiaryNavigator]

  "go to WhatTypeOfBeneficiaryPage from AddABeneficiaryYesNoPage when selected yes" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddABeneficiaryYesNoPage, true).success.value

        navigator.nextPage(AddABeneficiaryYesNoPage, fakeDraftId, answers)
          .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
    }
  }

  "go to WhatTypeOfBeneficiaryPage from AddABeneficiaryPage when selected add them now" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesNow).success.value

        navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
          .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
    }
  }

  "go to RegistrationProgress from AddABeneficiaryYesNoPage when selected no" in {
    forAll(arbitrary[UserAnswers]) {
      userAnswers =>

        val answers = userAnswers.set(AddABeneficiaryYesNoPage, false).success.value

        navigator.nextPage(AddABeneficiaryYesNoPage, fakeDraftId, answers)
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

          navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

          navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
            .mustBe(assetsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }
  }

  "Individual Beneficiary" when {

    "there are no Individual Beneficiaries" must {
      "go to IndividualBeneficiaryNamePage for index 0 from WhatTypeOfBeneficiaryPage when Individual option selected " in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers
              .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value
              .remove(IndividualBeneficiaries).success.value
            navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
              .mustBe(individualRoutes.NameController.onPageLoad(0, fakeDraftId))
        }
      }
    }

    "there is at least one Individual Beneficiary" must {
      "go to the next IndividualBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when Individual option selected" in {

        val answers = emptyUserAnswers
          .set(NamePage(0), FullName("First", None, "Last")).success.value
          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Individual).success.value

        navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
          .mustBe(individualRoutes.NameController.onPageLoad(1, fakeDraftId))
      }
    }
  }

  "Class of Beneficiaries" when {

    "there are no Class of Beneficiaries" must {
      "go to ClassBeneficiaryDescriptionPage for index 0 from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected " in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers
              .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value
              .remove(ClassOfBeneficiaries).success.value
            navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
              .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(0, fakeDraftId))
        }
      }
    }

    "there is at least one Class of beneficiary" must {
      "go to the next ClassBeneficiaryDescriptionPage from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected" in {

        val answers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
          .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.ClassOfBeneficiary).success.value

        navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
          .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(1, fakeDraftId))
      }
    }
  }

  "Company or employment related" when {
    "go to CompanyOrEmploymentRelated from WhatTypeOfBeneficiaryPage when CompanyOrEmploymentRelated option selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CompanyOrEmployment).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(companyOrEmploymentRelatedRoutes.CompanyOrEmploymentRelatedController.onPageLoad(fakeDraftId))
      }
    }

    "Company" when {
      "no existing company beneficiaries" must {
        "go to CompanyNamePage from WhatTypeOfBeneficiaryPage when 'company' option selected" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Company).success.value
                .remove(CompanyBeneficiaries).success.value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
          }
        }
        "go to CompanyNamePage from CompanyOrEmploymentRelatedPage when 'company' selected" in {
          val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, value = CompanyOrEmploymentRelatedToAdd.Company).success.value

          navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
            .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
        }
      }

      "existing company beneficiary" must {
        val baseAnswers = emptyUserAnswers
          .set(company.NamePage(0), "Name").success.value
          .set(CompanyBeneficiaryStatus(0), InProgress).success.value
          .set(CompanyOrEmploymentRelatedPage, value = CompanyOrEmploymentRelatedToAdd.Company).success.value

        "go to CompanyNamePage from WhatTypeOfBeneficiaryPage when 'company' option selected" in {
          val answers = baseAnswers
            .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Company).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(companyRoutes.NameController.onPageLoad(1, fakeDraftId))
        }

        "go to CompanyNamePage from CompanyOrEmploymentRelatedPage when 'company' selected" in {
          navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, baseAnswers)
            .mustBe(companyRoutes.NameController.onPageLoad(1, fakeDraftId))
        }
      }
    }

    "Employment related" when {
      "no existing employment related beneficiaries" must {
        "go to LargeBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when 'employment' option selected" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Employment).success.value
                .remove(LargeBeneficiaries).success.value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
          }
        }
        "go to LargeBeneficiaryNamePage from CompanyOrEmploymentRelatedPage when 'employment related' selected" in {
          val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, value = CompanyOrEmploymentRelatedToAdd.EmploymentRelated).success.value

          navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
            .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
        }
      }

      "existing employment related beneficiary" must {
        val baseAnswers = emptyUserAnswers
          .set(LargeBeneficiaryNamePage(0), "Name").success.value
          .set(LargeBeneficiaryStatus(0), InProgress).success.value
          .set(CompanyOrEmploymentRelatedPage, value = CompanyOrEmploymentRelatedToAdd.EmploymentRelated).success.value

        "go to LargeBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when 'employment related' option selected" in {
          val answers = baseAnswers
            .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Employment).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(largeRoutes.NameController.onPageLoad(1, fakeDraftId))
        }

        "go to LargeBeneficiaryNamePage from CompanyOrEmploymentRelatedPage when 'employment related' selected" in {
          navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, baseAnswers)
            .mustBe(largeRoutes.NameController.onPageLoad(1, fakeDraftId))
        }
      }
    }
  }

  "Charity or trust" when {
    "go to CharityOrTrustPage from WhatTypeOfBeneficiaryPage when 'charity or trust' selected" in {
      val answers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.CharityOrTrust).success.value

      navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
        .mustBe(charityortrustRoutes.CharityOrTrustController.onPageLoad(fakeDraftId))
    }

    "Charity" when {
      "no existing charity beneficiaries" must {
        "go to CharityNamePage from WhatTypeOfBeneficiaryPage when 'charity' selected" in {
          val answers = emptyUserAnswers
            .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Charity).success.value
            .remove(CharityBeneficiaries).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
        }
        "go to CharityNamePage from CharityOrTrustPage when 'charity' selected" in {
          val answers = emptyUserAnswers.set(CharityOrTrustPage, value = CharityOrTrust.Charity).success.value

          navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
            .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
        }
      }

      "existing charity beneficiary" must {
        val baseAnswers = emptyUserAnswers
          .set(CharityNamePage(0), "Name").success.value
          .set(CharityBeneficiaryStatus(0), InProgress).success.value
          .set(CharityOrTrustPage, value = CharityOrTrust.Charity).success.value

        "go to CharityNamePage from WhatTypeOfBeneficiaryPage when 'charity' selected" in {
          val answers = baseAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Charity).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(charityRoutes.CharityNameController.onPageLoad(1, fakeDraftId))
        }

        "go to CharityNamePage from CharityOrTrustPage when 'charity' selected" in {
          navigator.nextPage(CharityOrTrustPage, fakeDraftId, baseAnswers)
            .mustBe(charityRoutes.CharityNameController.onPageLoad(1, fakeDraftId))
        }
      }
    }

    "Trust" when {
      "no existing trust beneficiaries" must {
        "go to TrustNamePage from WhatTypeOfBeneficiaryPage when 'trust' selected" in {
          val answers = emptyUserAnswers
            .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Trust).success.value
            .remove(TrustBeneficiaries).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
        }
        "go to TrustNamePage from CharityOrTrustPage when 'trust' selected" in {
          val answers = emptyUserAnswers.set(CharityOrTrustPage, value = CharityOrTrust.Trust).success.value

          navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
            .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
        }
      }

      "existing trust beneficiary" must {
        val baseAnswers = emptyUserAnswers
          .set(trust.NamePage(0), "Name").success.value
          .set(TrustBeneficiaryStatus(0), InProgress).success.value
          .set(CharityOrTrustPage, value = CharityOrTrust.Trust).success.value

        "go to TrustNamePage from WhatTypeOfBeneficiaryPage when 'trust' selected" in {
          val answers = baseAnswers.set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Trust).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(trustRoutes.NameController.onPageLoad(1, fakeDraftId))
        }

        "go to TrustNamePage from CharityOrTrustPage when 'trust' selected" in {
          navigator.nextPage(CharityOrTrustPage, fakeDraftId, baseAnswers)
            .mustBe(trustRoutes.NameController.onPageLoad(1, fakeDraftId))
        }
      }
    }
    "Other Beneficiary" when {

      "there are no Other Beneficiaries" must {
        "go to description page for index 0 from WhatTypeOfBeneficiaryPage when Other option selected " in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Other).success.value
                .remove(OtherBeneficiaries).success.value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(otherRoutes.DescriptionController.onPageLoad(0, fakeDraftId))
          }
        }
      }

      "there is at least one Other Beneficiary" must {
        "go to the next description page from WhatTypeOfBeneficiaryPage when Other option selected" in {

          val answers = emptyUserAnswers
            .set(DescriptionPage(0), "Other description").success.value
            .set(WhatTypeOfBeneficiaryPage, value = WhatTypeOfBeneficiary.Other).success.value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(otherRoutes.DescriptionController.onPageLoad(1, fakeDraftId))
        }
      }
    }

  }

}

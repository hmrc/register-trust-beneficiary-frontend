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
import models.Status.Completed
import models.core.pages.FullName
import models.registration.pages.{AddABeneficiary, CharityOrTrust, WhatTypeOfBeneficiary}
import models.{CompanyOrEmploymentRelatedToAdd, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.entitystatus._
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
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.Constants.MAX

class BeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private lazy val taskListRoute: Call = {
    Call(GET, frontendAppConfig.registrationProgressUrl(fakeDraftId))
  }

  val navigator: BeneficiaryNavigator = injector.instanceOf[BeneficiaryNavigator]

  "BeneficiaryNavigator" when {

    "AddABeneficiaryYesNoPage" when {

      "yes selected" must {
        "redirect to WhatTypeOfBeneficiaryController" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(AddABeneficiaryYesNoPage, true).right.get

              navigator.nextPage(AddABeneficiaryYesNoPage, fakeDraftId, answers)
                .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
          }
        }
      }

      "no selected" must {
        "redirect to task list" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(AddABeneficiaryYesNoPage, false).right.get

              navigator.nextPage(AddABeneficiaryYesNoPage, fakeDraftId, answers)
                .mustBe(taskListRoute)
          }
        }
      }
    }

    "AddABeneficiaryPage" when {

      "YesNow selected" when {

        "all types maxed out except individual" must {
          "redirect to individual journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(individualRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "all types maxed out except unidentified" must {
          "redirect to unidentified journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "all types maxed out except company" must {
          "redirect to company journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "all types maxed out except employment-related" must {
          "redirect to employment-related journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "all types maxed out except company and employment-related" must {
          "redirect to company or employment-related page" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(companyOrEmploymentRelatedRoutes.CompanyOrEmploymentRelatedController.onPageLoad(fakeDraftId))
            }
          }
        }

        "all types maxed out except trust" must {
          "redirect to trust journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "all types maxed out except charity" must {
          "redirect to charity journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "all types maxed out except trust and charity" must {
          "redirect to charity or trust page" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(DescriptionPage(i), "Other description").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(charityortrustRoutes.CharityOrTrustController.onPageLoad(fakeDraftId))
            }
          }
        }

        "all types maxed out except other" must {
          "redirect to other journey" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                    .set(trust.NamePage(i), "Name").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(otherRoutes.DescriptionController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "no types maxed out" must {
          "go to WhatTypeOfBeneficiaryPage" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
            }
          }
        }

        "one type maxed out" must {
          "go to WhatTypeOfBeneficiaryPage" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
            }
          }
        }

        "all types maxed out bar 2" must {
          "go to WhatTypeOfBeneficiaryPage" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = (0 until MAX).foldLeft(userAnswers)((acc, i) => {
                  acc
                    .set(NamePage(i), FullName("First", None, "Last")).right.get
                    .set(ClassBeneficiaryDescriptionPage(i), "description").right.get
                    .set(company.NamePage(i), "Name").right.get
                    .set(LargeBeneficiaryNamePage(i), "Name").right.get
                    .set(CharityNamePage(i), "Name").right.get
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).right.get

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
            }
          }
        }
      }

      "YesLater selected" must {
        "redirect to task list" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesLater).right.get

              navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                .mustBe(taskListRoute)
          }
        }
      }

      "NoComplete selected" must {
        "redirect to task list" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.NoComplete).right.get

              navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                .mustBe(taskListRoute)
          }
        }
      }
    }

    "WhatTypeOfBeneficiaryPage" when {

      "Individual Beneficiary" when {

        "there are no Individual Beneficiaries" must {
          "go to IndividualBeneficiaryNamePage for index 0 from WhatTypeOfBeneficiaryPage when Individual option selected " in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers
                  .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).right.get
                  .remove(IndividualBeneficiaries).right.get
                navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                  .mustBe(individualRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "there are Individual Beneficiaries" must {
          "go to the next IndividualBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when Individual option selected" in {

            val answers = emptyUserAnswers
              .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).right.get
              .set(NamePage(0), FullName("First", None, "Last")).right.get
              .set(IndividualBeneficiaryStatus(0), Completed).right.get

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
                  .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).right.get
                  .remove(ClassOfBeneficiaries).right.get
                navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                  .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "there are Class of Beneficiaries" must {
          "go to the next ClassBeneficiaryDescriptionPage from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected" in {

            val answers = emptyUserAnswers
              .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).right.get
              .set(ClassBeneficiaryDescriptionPage(0), "description").right.get
              .set(ClassBeneficiaryStatus(0), Completed).right.get

            navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
              .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(1, fakeDraftId))
          }
        }
      }

      "Company or employment related" when {
        "go to CompanyOrEmploymentRelated from WhatTypeOfBeneficiaryPage when CompanyOrEmploymentRelated option selected" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).right.get

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
                    .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Company).right.get
                    .remove(CompanyBeneficiaries).right.get

                  navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                    .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
              }
            }
            "go to CompanyNamePage from CompanyOrEmploymentRelatedPage when 'company' selected" in {
              val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.Company).right.get

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing company beneficiary" must {

            "go to CompanyNamePage from WhatTypeOfBeneficiaryPage when 'company' option selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Company).right.get
                .set(company.NamePage(0), "Name").right.get
                .set(CompanyBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(companyRoutes.NameController.onPageLoad(1, fakeDraftId))
            }

            "go to CompanyNamePage from CompanyOrEmploymentRelatedPage when 'company' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).right.get
                .set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.Company).right.get
                .set(company.NamePage(0), "Name").right.get
                .set(CompanyBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(companyRoutes.NameController.onPageLoad(1, fakeDraftId))
            }
          }
        }

        "Employment-related" when {
          "no existing employment-related beneficiaries" must {
            "go to LargeBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when 'employment' option selected" in {
              forAll(arbitrary[UserAnswers]) {
                userAnswers =>
                  val answers = userAnswers
                    .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Employment).right.get
                    .remove(LargeBeneficiaries).right.get

                  navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                    .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
              }
            }
            "go to LargeBeneficiaryNamePage from CompanyOrEmploymentRelatedPage when 'employment related' selected" in {
              val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.EmploymentRelated).right.get

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing employment-related beneficiary" must {

            "go to LargeBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when 'employment related' option selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Employment).right.get
                .set(LargeBeneficiaryNamePage(0), "Name").right.get
                .set(LargeBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(1, fakeDraftId))
            }

            "go to LargeBeneficiaryNamePage from CompanyOrEmploymentRelatedPage when 'employment related' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).right.get
                .set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.EmploymentRelated).right.get
                .set(LargeBeneficiaryNamePage(0), "Name").right.get
                .set(LargeBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(1, fakeDraftId))
            }
          }
        }
      }

      "Charity or trust" when {
        "go to CharityOrTrustPage from WhatTypeOfBeneficiaryPage when 'charity or trust' selected" in {
          val answers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).right.get

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(charityortrustRoutes.CharityOrTrustController.onPageLoad(fakeDraftId))
        }

        "Charity" when {
          "no existing charity beneficiaries" must {
            "go to CharityNamePage from WhatTypeOfBeneficiaryPage when 'charity' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Charity).right.get
                .remove(CharityBeneficiaries).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
            }
            "go to CharityNamePage from CharityOrTrustPage when 'charity' selected" in {
              val answers = emptyUserAnswers.set(CharityOrTrustPage, CharityOrTrust.Charity).right.get

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing charity beneficiary" must {

            "go to CharityNamePage from WhatTypeOfBeneficiaryPage when 'charity' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Charity).right.get
                .set(CharityNamePage(0), "Name").right.get
                .set(CharityBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(1, fakeDraftId))
            }

            "go to CharityNamePage from CharityOrTrustPage when 'charity' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).right.get
                .set(CharityOrTrustPage, CharityOrTrust.Charity).right.get
                .set(CharityNamePage(0), "Name").right.get
                .set(CharityBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(1, fakeDraftId))
            }
          }
        }

        "Trust" when {
          "no existing trust beneficiaries" must {
            "go to TrustNamePage from WhatTypeOfBeneficiaryPage when 'trust' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Trust).right.get
                .remove(TrustBeneficiaries).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
            "go to TrustNamePage from CharityOrTrustPage when 'trust' selected" in {
              val answers = emptyUserAnswers.set(CharityOrTrustPage, CharityOrTrust.Trust).right.get

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
                .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing trust beneficiary" must {

            "go to TrustNamePage from WhatTypeOfBeneficiaryPage when 'trust' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Trust).right.get
                .set(trust.NamePage(0), "Name").right.get
                .set(TrustBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(trustRoutes.NameController.onPageLoad(1, fakeDraftId))
            }

            "go to TrustNamePage from CharityOrTrustPage when 'trust' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).right.get
                .set(CharityOrTrustPage, CharityOrTrust.Trust).right.get
                .set(trust.NamePage(0), "Name").right.get
                .set(TrustBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
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
                    .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Other).right.get
                    .remove(OtherBeneficiaries).right.get

                  navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                    .mustBe(otherRoutes.DescriptionController.onPageLoad(0, fakeDraftId))
              }
            }
          }

          "there are Other Beneficiaries" must {
            "go to the next description page from WhatTypeOfBeneficiaryPage when Other option selected" in {

              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Other).right.get
                .set(DescriptionPage(0), "Other description").right.get
                .set(OtherBeneficiaryStatus(0), Completed).right.get

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(otherRoutes.DescriptionController.onPageLoad(1, fakeDraftId))
            }
          }
        }
      }
    }
  }
}

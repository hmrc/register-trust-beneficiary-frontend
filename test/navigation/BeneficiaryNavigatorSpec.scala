/*
 * Copyright 2023 HM Revenue & Customs
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
              val answers = userAnswers.set(AddABeneficiaryYesNoPage, true).value

              navigator.nextPage(AddABeneficiaryYesNoPage, fakeDraftId, answers)
                .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
          }
        }
      }

      "no selected" must {
        "redirect to task list" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(AddABeneficiaryYesNoPage, false).value

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
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(CharityNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(DescriptionPage(i), "Other description").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                    .set(trust.NamePage(i), "Name").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

                navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                  .mustBe(otherRoutes.DescriptionController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "no types maxed out" must {
          "go to WhatTypeOfBeneficiaryPage" in {
            forAll(arbitrary[UserAnswers]) {
              userAnswers =>
                val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
                    .set(NamePage(i), FullName("First", None, "Last")).value
                    .set(ClassBeneficiaryDescriptionPage(i), "description").value
                    .set(company.NamePage(i), "Name").value
                    .set(LargeBeneficiaryNamePage(i), "Name").value
                    .set(CharityNamePage(i), "Name").value
                }).set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

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
              val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.YesLater).value

              navigator.nextPage(AddABeneficiaryPage, fakeDraftId, answers)
                .mustBe(taskListRoute)
          }
        }
      }

      "NoComplete selected" must {
        "redirect to task list" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(AddABeneficiaryPage, AddABeneficiary.NoComplete).value

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
                  .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).value
                  .remove(IndividualBeneficiaries).value
                navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                  .mustBe(individualRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "there are Individual Beneficiaries" must {
          "go to the next IndividualBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when Individual option selected" in {

            val answers = emptyUserAnswers
              .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).value
              .set(NamePage(0), FullName("First", None, "Last")).value
              .set(IndividualBeneficiaryStatus(0), Completed).value

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
                  .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).value
                  .remove(ClassOfBeneficiaries).value
                navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                  .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(0, fakeDraftId))
            }
          }
        }

        "there are Class of Beneficiaries" must {
          "go to the next ClassBeneficiaryDescriptionPage from WhatTypeOfBeneficiaryPage when ClassOfBeneficiary option selected" in {

            val answers = emptyUserAnswers
              .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).value
              .set(ClassBeneficiaryDescriptionPage(0), "description").value
              .set(ClassBeneficiaryStatus(0), Completed).value

            navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
              .mustBe(classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(1, fakeDraftId))
          }
        }
      }

      "Company or employment related" when {
        "go to CompanyOrEmploymentRelated from WhatTypeOfBeneficiaryPage when CompanyOrEmploymentRelated option selected" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).value

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
                    .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Company).value
                    .remove(CompanyBeneficiaries).value

                  navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                    .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
              }
            }
            "go to CompanyNamePage from CompanyOrEmploymentRelatedPage when 'company' selected" in {
              val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.Company).value

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(companyRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing company beneficiary" must {

            "go to CompanyNamePage from WhatTypeOfBeneficiaryPage when 'company' option selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Company).value
                .set(company.NamePage(0), "Name").value
                .set(CompanyBeneficiaryStatus(0), Completed).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(companyRoutes.NameController.onPageLoad(1, fakeDraftId))
            }

            "go to CompanyNamePage from CompanyOrEmploymentRelatedPage when 'company' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).value
                .set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.Company).value
                .set(company.NamePage(0), "Name").value
                .set(CompanyBeneficiaryStatus(0), Completed).value

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
                    .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Employment).value
                    .remove(LargeBeneficiaries).value

                  navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                    .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
              }
            }
            "go to LargeBeneficiaryNamePage from CompanyOrEmploymentRelatedPage when 'employment related' selected" in {
              val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.EmploymentRelated).value

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing employment-related beneficiary" must {

            "go to LargeBeneficiaryNamePage from WhatTypeOfBeneficiaryPage when 'employment related' option selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Employment).value
                .set(LargeBeneficiaryNamePage(0), "Name").value
                .set(LargeBeneficiaryStatus(0), Completed).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(1, fakeDraftId))
            }

            "go to LargeBeneficiaryNamePage from CompanyOrEmploymentRelatedPage when 'employment related' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).value
                .set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.EmploymentRelated).value
                .set(LargeBeneficiaryNamePage(0), "Name").value
                .set(LargeBeneficiaryStatus(0), Completed).value

              navigator.nextPage(CompanyOrEmploymentRelatedPage, fakeDraftId, answers)
                .mustBe(largeRoutes.NameController.onPageLoad(1, fakeDraftId))
            }
          }
        }
      }

      "Charity or trust" when {
        "go to CharityOrTrustPage from WhatTypeOfBeneficiaryPage when 'charity or trust' selected" in {
          val answers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).value

          navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
            .mustBe(charityortrustRoutes.CharityOrTrustController.onPageLoad(fakeDraftId))
        }

        "Charity" when {
          "no existing charity beneficiaries" must {
            "go to CharityNamePage from WhatTypeOfBeneficiaryPage when 'charity' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Charity).value
                .remove(CharityBeneficiaries).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
            }
            "go to CharityNamePage from CharityOrTrustPage when 'charity' selected" in {
              val answers = emptyUserAnswers.set(CharityOrTrustPage, CharityOrTrust.Charity).value

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing charity beneficiary" must {

            "go to CharityNamePage from WhatTypeOfBeneficiaryPage when 'charity' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Charity).value
                .set(CharityNamePage(0), "Name").value
                .set(CharityBeneficiaryStatus(0), Completed).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(1, fakeDraftId))
            }

            "go to CharityNamePage from CharityOrTrustPage when 'charity' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).value
                .set(CharityOrTrustPage, CharityOrTrust.Charity).value
                .set(CharityNamePage(0), "Name").value
                .set(CharityBeneficiaryStatus(0), Completed).value

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
                .mustBe(charityRoutes.CharityNameController.onPageLoad(1, fakeDraftId))
            }
          }
        }

        "Trust" when {
          "no existing trust beneficiaries" must {
            "go to TrustNamePage from WhatTypeOfBeneficiaryPage when 'trust' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Trust).value
                .remove(TrustBeneficiaries).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
            "go to TrustNamePage from CharityOrTrustPage when 'trust' selected" in {
              val answers = emptyUserAnswers.set(CharityOrTrustPage, CharityOrTrust.Trust).value

              navigator.nextPage(CharityOrTrustPage, fakeDraftId, answers)
                .mustBe(trustRoutes.NameController.onPageLoad(0, fakeDraftId))
            }
          }

          "existing trust beneficiary" must {

            "go to TrustNamePage from WhatTypeOfBeneficiaryPage when 'trust' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Trust).value
                .set(trust.NamePage(0), "Name").value
                .set(TrustBeneficiaryStatus(0), Completed).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(trustRoutes.NameController.onPageLoad(1, fakeDraftId))
            }

            "go to TrustNamePage from CharityOrTrustPage when 'trust' selected" in {
              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).value
                .set(CharityOrTrustPage, CharityOrTrust.Trust).value
                .set(trust.NamePage(0), "Name").value
                .set(TrustBeneficiaryStatus(0), Completed).value

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
                    .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Other).value
                    .remove(OtherBeneficiaries).value

                  navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                    .mustBe(otherRoutes.DescriptionController.onPageLoad(0, fakeDraftId))
              }
            }
          }

          "there are Other Beneficiaries" must {
            "go to the next description page from WhatTypeOfBeneficiaryPage when Other option selected" in {

              val answers = emptyUserAnswers
                .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Other).value
                .set(DescriptionPage(0), "Other description").value
                .set(OtherBeneficiaryStatus(0), Completed).value

              navigator.nextPage(WhatTypeOfBeneficiaryPage, fakeDraftId, answers)
                .mustBe(otherRoutes.DescriptionController.onPageLoad(1, fakeDraftId))
            }
          }
        }
      }
    }
  }
}

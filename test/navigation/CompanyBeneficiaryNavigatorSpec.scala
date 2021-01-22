/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.register.beneficiaries.companyoremploymentrelated.company.nonTaxable.{routes => ntrts}
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.companyoremploymentrelated.company.nonTaxable._
import pages.register.beneficiaries.companyoremploymentrelated.company._

class CompanyBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new CompanyBeneficiaryNavigator
  val index = 0

  "Company beneficiary navigator" when {

    "a 4mld trust" must {

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
            val answers = baseAnswers.set(IncomeYesNoPage(index), true).success.value
            navigator.nextPage(IncomeYesNoPage(index), fakeDraftId, answers)
              .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to ShareOfIncomePage from DiscretionYesNo if No" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>
            val answers = baseAnswers.set(IncomeYesNoPage(index), false).success.value
            navigator.nextPage(IncomeYesNoPage(index), fakeDraftId, answers)
              .mustBe(CompanyRoutes.ShareOfIncomeController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to AddressYesNo from ShareOfIncomePage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(IncomePage(index), fakeDraftId, userAnswers)
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

    "a 5mld trust" when {

      "a taxable trust" must {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)

        "Discretion yes no page -> Yes -> CountryOfResidence Yes No page" in {
          val answers = baseAnswers
            .set(IncomeYesNoPage(index), true).success.value

          navigator.nextPage(IncomeYesNoPage(index), draftId, answers)
            .mustBe(ntrts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(ntrts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }


        "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence Uk yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(ntrts.CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidence page -> Address yes no page" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(CompanyRoutes.AddressYesNoController.onPageLoad(index, draftId))
        }

        "ShareOfIncome page -> CountryOfResidence Yes No page" in {
          navigator.nextPage(IncomePage(index), draftId, baseAnswers)
            .mustBe(ntrts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }
      }
    }
  }
}

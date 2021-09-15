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
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => companyRoutes}
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.companyoremploymentrelated.company._

class CompanyBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new CompanyBeneficiaryNavigator
  val index = 0

  "Company beneficiary navigator" when {


      "a taxable trust" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

        "Discretion yes no page -> Yes -> CountryOfResidence Yes No page" in {
          val answers = baseAnswers
            .set(IncomeYesNoPage(index), true).success.value

          navigator.nextPage(IncomeYesNoPage(index), draftId, answers)
            .mustBe(companyRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(companyRoutes.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(companyRoutes.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }


        "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(companyRoutes.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence Uk yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(companyRoutes.CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidence page -> Address yes no page" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(companyRoutes.AddressYesNoController.onPageLoad(index, draftId))
        }

        "ShareOfIncome page -> CountryOfResidence Yes No page" in {
          navigator.nextPage(IncomePage(index), draftId, baseAnswers)
            .mustBe(companyRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }
      }

      "a non-taxable trust" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "NamePage -> CountryOfResidenceYesNoPage" in {
          navigator.nextPage(NamePage(index), draftId, baseAnswers)
            .mustBe(companyRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceYesNoPage" when {

          val page = CountryOfResidenceYesNoPage(index)

          "-> Yes -> CountryOfResidenceInTheUkYesNoPage" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, draftId, answers)
              .mustBe(companyRoutes.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
          }

          "-> No -> CheckDetails" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, draftId, answers)
              .mustBe(companyRoutes.CheckDetailsController.onPageLoad(index, draftId))
          }
        }

        "CountryOfResidenceInTheUkYesNoPage" when {

          val page = CountryOfResidenceInTheUkYesNoPage(index)

          "-> Yes -> CheckDetails" in {
            val answers = baseAnswers
              .set(page, true).success.value

            navigator.nextPage(page, draftId, answers)
              .mustBe(companyRoutes.CheckDetailsController.onPageLoad(index, draftId))
          }

          "-> No -> CountryOfResidencePage" in {
            val answers = baseAnswers
              .set(page, false).success.value

            navigator.nextPage(page, draftId, answers)
              .mustBe(companyRoutes.CountryOfResidenceController.onPageLoad(index, draftId))
          }
        }

        "CountryOfResidencePage -> CheckDetails" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(companyRoutes.CheckDetailsController.onPageLoad(index, draftId))
        }
      }
    }
}

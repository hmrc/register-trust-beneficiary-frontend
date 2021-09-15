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
import controllers.register.beneficiaries.charityortrust.trust.{routes => rts}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.charityortrust.trust._

class TrustBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new TrustBeneficiaryNavigator()

  val index = 0

  "Trust beneficiary navigator" when {

      "a taxable trust" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

        "Discretion yes no page -> Yes -> CountryOfResidence Yes No page" in {
          val answers = baseAnswers
            .set(DiscretionYesNoPage(index), true).success.value

          navigator.nextPage(DiscretionYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }


        "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidence page -> Address yes no page" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
        }

        "ShareOfIncome page -> CountryOfResidence Yes No page" in {
          navigator.nextPage(ShareOfIncomePage(index), draftId, baseAnswers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }
      }

      "a non taxable trust" must {

        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "Name page -> CountryOfResidence Yes No page" in {
          val answers = baseAnswers
            .set(NamePage(index), "Trust Name").success.value

          navigator.nextPage(NamePage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Check your answers page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.AnswersController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }


        "CountryOfResidence Uk yes no page -> Yes -> Check your answers page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.AnswersController.onPageLoad(index, draftId))
        }

        "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidence page -> Check your answers page" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(rts.AnswersController.onPageLoad(index, draftId))
        }
      }
  }
}

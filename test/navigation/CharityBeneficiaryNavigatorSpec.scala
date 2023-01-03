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
import controllers.register.beneficiaries.charityortrust.charity.mld5.routes._
import controllers.register.beneficiaries.charityortrust.charity.routes._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.charity.mld5._

class CharityBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new CharityBeneficiaryNavigator

  val index = 0

  "Charity beneficiary navigator" when {

    val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

    "Name page -> Do you know date of birth page" in {
      navigator.nextPage(CharityNamePage(index), fakeDraftId, baseAnswers)
        .mustBe(AmountDiscretionYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Do trustees have discretion page" when {

      "Yes" must {
        "-> Country of residence yes no page" in {
          val answers = baseAnswers
            .set(AmountDiscretionYesNoPage(index), true).right.get

          navigator.nextPage(AmountDiscretionYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }
      }

      "No" must {
        "-> How much income page" in {
          val answers = baseAnswers
            .set(AmountDiscretionYesNoPage(index), false).right.get

          navigator.nextPage(AmountDiscretionYesNoPage(index), fakeDraftId, answers)
            .mustBe(HowMuchIncomeController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "Do you know address page" when {

      "Yes" must {
        "-> Is address in UK page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage(index), true).right.get

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "No" must {
        "-> Check answers page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage(index), false).right.get

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(CharityAnswersController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "Is address in UK page" when {

      "Yes" must {
        "-> UK address page" in {
          val answers = baseAnswers
            .set(AddressInTheUkYesNoPage(index), true).right.get

          navigator.nextPage(AddressInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CharityAddressUKController.onPageLoad(index, fakeDraftId))
        }
      }

      "No" must {
        "-> International address page" in {
          val answers = baseAnswers
            .set(AddressInTheUkYesNoPage(index), false).right.get

          navigator.nextPage(AddressInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CharityInternationalAddressController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "UK address page -> Check answers page" in {
      navigator.nextPage(CharityAddressUKPage(index), fakeDraftId, baseAnswers)
        .mustBe(CharityAnswersController.onPageLoad(index, fakeDraftId))
    }

    "International address page -> Check answers page" in {
      navigator.nextPage(CharityInternationalAddressPage(index), fakeDraftId, baseAnswers)
        .mustBe(CharityAnswersController.onPageLoad(index, fakeDraftId))
    }

    "a taxable trust" must {

      "CountryOfResidence yes no page -> No -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
      }


      "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).right.get

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence Uk yes no page -> No -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).right.get

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "CountryOfResidence page -> Address yes no page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
          .mustBe(AddressYesNoController.onPageLoad(index, draftId))
      }

      "ShareOfIncome page -> CountryOfResidence Yes No page" in {

        navigator.nextPage(HowMuchIncomePage(index), draftId, baseAnswers)
          .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }
    }

    "a non taxable trust" must {

      val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

      "Charity Name page -> CountryOfResidence Yes No page" in {
        val answers = baseAnswers
          .set(CharityNamePage(index), "Charity Name").right.get

        navigator.nextPage(CharityNamePage(index), draftId, answers)
          .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> No -> Check your answers page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(CharityAnswersController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
      }


      "CountryOfResidence Uk yes no page -> Yes -> Check your answers page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).right.get

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(CharityAnswersController.onPageLoad(index, draftId))
      }

      "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).right.get

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "CountryOfResidence page -> Check your answers page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
          .mustBe(CharityAnswersController.onPageLoad(index, draftId))
      }
    }
  }
}

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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.other._
import controllers.register.beneficiaries.other.routes._

class OtherBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  private val index = 0
  private val navigator = new OtherBeneficiaryNavigator

  "Other beneficiary navigator" when {

    "Name page -> Discretion yes no page" in {
      navigator.nextPage(DescriptionPage(index), draftId, emptyUserAnswers)
        .mustBe(DiscretionYesNoController.onPageLoad(index, draftId))
    }

    "Discretion yes no page -> Yes -> Address yes no page" in {
      val answers = emptyUserAnswers
        .set(IncomeDiscretionYesNoPage(index), true).success.value

      navigator.nextPage(IncomeDiscretionYesNoPage(index), draftId, answers)
        .mustBe(AddressYesNoController.onPageLoad(index, draftId))
    }

    "Discretion yes no page -> No -> Share of income page" in {
      val answers = emptyUserAnswers
        .set(IncomeDiscretionYesNoPage(index), false).success.value

      navigator.nextPage(IncomeDiscretionYesNoPage(index), draftId, answers)
        .mustBe(ShareOfIncomeController.onPageLoad(index, draftId))
    }

    "Share of income page -> Address yes no page" in {
      navigator.nextPage(ShareOfIncomePage(index), draftId, emptyUserAnswers)
        .mustBe(AddressYesNoController.onPageLoad(index, draftId))
    }

    "Address yes no page -> No -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage(index), false).success.value

      navigator.nextPage(AddressYesNoPage(index), draftId, answers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }

    "Address yes no page -> Yes -> Address in the UK yes no page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage(index), true).success.value

      navigator.nextPage(AddressYesNoPage(index), draftId, answers)
        .mustBe(AddressUkYesNoController.onPageLoad(index, draftId))
    }

    "Address in the UK yes no page -> Yes -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUKYesNoPage(index), true).success.value

      navigator.nextPage(AddressUKYesNoPage(index), draftId, answers)
        .mustBe(UkAddressController.onPageLoad(index, draftId))
    }

    "Address in the UK yes no page -> No -> Non-UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUKYesNoPage(index), false).success.value

      navigator.nextPage(AddressUKYesNoPage(index), draftId, answers)
        .mustBe(NonUkAddressController.onPageLoad(index, draftId))
    }

    "UK address page -> Check your answers page" in {
      navigator.nextPage(AddressUKPage(index), draftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }

    "Non-UK address page -> Check your answers page" in {
      navigator.nextPage(AddressInternationalPage(index), draftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }
  }
}

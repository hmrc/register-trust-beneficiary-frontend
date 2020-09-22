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

import controllers.register.beneficiaries.charityortrust.charity.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.charityortrust.charity._
import play.api.mvc.Call

class CharityBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case CharityNamePage(index) => AmountDiscretionYesNoController.onPageLoad(index, draftId)
    case HowMuchIncomePage(index) => AddressYesNoController.onPageLoad(index, draftId)
    case CharityAddressUKPage(index) => CharityAnswersController.onPageLoad(index, draftId)
    case CharityInternationalAddressPage(index) => CharityAnswersController.onPageLoad(index, draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AmountDiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AmountDiscretionYesNoPage(index),
        AddressYesNoController.onPageLoad(index, draftId),
        HowMuchIncomeController.onPageLoad(index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        AddressInTheUkYesNoController.onPageLoad(index, draftId),
        CharityAnswersController.onPageLoad(index, draftId)
      )
    case AddressInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressInTheUkYesNoPage(index),
        CharityAddressUKController.onPageLoad(index, draftId),
        CharityInternationalAddressController.onPageLoad(index, draftId)
      )
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      conditionalNavigation(draftId)
}

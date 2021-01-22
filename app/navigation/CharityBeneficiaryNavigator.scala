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

import controllers.register.beneficiaries.charityortrust.charity.nonTaxable.routes._
import controllers.register.beneficiaries.charityortrust.charity.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.charity.nonTaxable._
import play.api.mvc.Call

class CharityBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse yesNoNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case CharityNamePage(index) => _ => AmountDiscretionYesNoController.onPageLoad(index, draftId)
    case HowMuchIncomePage(index) => ua => navigateAwayFromShareOfIncomeQuestions(draftId, index, ua.is5mldEnabled)
    case CharityAddressUKPage(index) => _ => CharityAnswersController.onPageLoad(index, draftId)
    case CharityInternationalAddressPage(index) => _ => CharityAnswersController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => _ => AddressYesNoController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AmountDiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AmountDiscretionYesNoPage(index),
        yesCall = navigateAwayFromShareOfIncomeQuestions(draftId, index, ua.is5mldEnabled),
        noCall = HowMuchIncomeController.onPageLoad(index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AddressYesNoPage(index),
        yesCall = AddressInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = CharityAnswersController.onPageLoad(index, draftId)
      )
    case AddressInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AddressInTheUkYesNoPage(index),
        yesCall = CharityAddressUKController.onPageLoad(index, draftId),
        noCall = CharityInternationalAddressController.onPageLoad(index, draftId)
      )
    case CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceYesNoPage(index),
        yesCall = CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = AddressYesNoController.onPageLoad(index, draftId)
      )
    case CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceInTheUkYesNoPage(index),
        yesCall = AddressYesNoController.onPageLoad(index, draftId),
        noCall = CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateAwayFromShareOfIncomeQuestions(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      AddressYesNoController.onPageLoad(index, draftId)
    }
  }

}

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

import controllers.register.beneficiaries.charityortrust.charity.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.charityortrust.charity._
import play.api.mvc.Call

class CharityBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse yesNoNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case CharityNamePage(index) => ua =>
      if (isNonTaxable(ua)) {
        CountryOfResidenceYesNoController.onPageLoad(index, draftId)
      } else {
        AmountDiscretionYesNoController.onPageLoad(index, draftId)
      }
    case HowMuchIncomePage(index) => ua => CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    case CharityAddressUKPage(index) => _ => CharityAnswersController.onPageLoad(index, draftId)
    case CharityInternationalAddressPage(index) => _ => CharityAnswersController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => ua =>
      if (isNonTaxable(ua)) {
        CharityAnswersController.onPageLoad(index, draftId)
      } else {
        AddressYesNoController.onPageLoad(index, draftId)
      }
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ AmountDiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfResidenceYesNoController.onPageLoad(index, draftId),
        noCall = HowMuchIncomeController.onPageLoad(index, draftId)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = CharityAnswersController.onPageLoad(index, draftId)
      )
    case page @ AddressInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CharityAddressUKController.onPageLoad(index, draftId),
        noCall = CharityInternationalAddressController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateToAnswersOrAddressQuestions(draftId, index)(ua)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateToAnswersOrAddressQuestions(draftId, index)(ua),
        noCall = CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateToAnswersOrAddressQuestions(draftId: String, index: Int): PartialFunction[ReadableUserAnswers, Call] = {
    case ua =>
      if (isNonTaxable(ua)) {
        CharityAnswersController.onPageLoad(index, draftId)
      } else {
        AddressYesNoController.onPageLoad(index, draftId)
      }
  }

}

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

import controllers.register.beneficiaries.charityortrust.trust.{routes => rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.charityortrust.trust.{NamePage, _}
import play.api.mvc.Call

class TrustBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => ua =>
      if (isNonTaxable(ua)) {
        rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
      } else {
        rts.DiscretionYesNoController.onPageLoad(index, draftId)
      }
    case CountryOfResidencePage(index) => ua =>
      if (isNonTaxable(ua)) {
        rts.AnswersController.onPageLoad(index, draftId)
      } else {
        rts.AddressYesNoController.onPageLoad(index, draftId)
      }
    case ShareOfIncomePage(index) => _ => rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    case AddressUKPage(index) => _ => rts.AnswersController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => _ => rts.AnswersController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ DiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId),
        noCall = rts.ShareOfIncomeController.onPageLoad(index, draftId)
      )
    case page @ AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.AddressUKController.onPageLoad(index, draftId),
        noCall = rts.AddressInternationalController.onPageLoad(index, draftId)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.AddressUKYesNoController.onPageLoad(index, draftId),
        noCall = yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.AddressUKYesNoController.onPageLoad(index, draftId),
          noCall = rts.AnswersController.onPageLoad(index, draftId)
        )
      )
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateToAnswersOrAddressQuestions(draftId, index)(ua)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateToAnswersOrAddressQuestions(draftId, index)(ua),
        noCall = rts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateToAnswersOrAddressQuestions(draftId: String, index: Int): PartialFunction[ReadableUserAnswers, Call] = {
    case ua =>
      if (isNonTaxable(ua)) {
        rts.AnswersController.onPageLoad(index, draftId)
      } else {
        rts.AddressYesNoController.onPageLoad(index, draftId)
      }
  }

}

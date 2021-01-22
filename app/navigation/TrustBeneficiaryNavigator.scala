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

import controllers.register.beneficiaries.charityortrust.trust.nonTaxable.{routes => nonTaxRts}
import controllers.register.beneficiaries.charityortrust.trust.{routes => rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.charityortrust.trust.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
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
      if (is5mldNonTaxable(ua)) {
        nonTaxRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
      } else {
        rts.DiscretionYesNoController.onPageLoad(index, draftId)
      }
    case CountryOfResidencePage(index) => ua =>
      if (is5mldNonTaxable(ua)) {
        rts.AnswersController.onPageLoad(index, draftId)
      } else {
        rts.AddressYesNoController.onPageLoad(index, draftId)
      }
    case ShareOfIncomePage(index) => ua => navigateAwayFromShareOfIncomeQuestions(draftId, index, ua.is5mldEnabled)
    case AddressUKPage(index) => _ => rts.AnswersController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => _ => rts.AnswersController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = DiscretionYesNoPage(index),
        yesCall = navigateAwayFromShareOfIncomeQuestions(draftId, index, ua.is5mldEnabled),
        noCall = rts.ShareOfIncomeController.onPageLoad(index, draftId)
      )
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AddressUKYesNoPage(index),
        yesCall = rts.AddressUKController.onPageLoad(index, draftId),
        noCall = rts.AddressInternationalController.onPageLoad(index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = AddressYesNoPage(index),
        yesCall = rts.AddressUKYesNoController.onPageLoad(index, draftId),
        noCall = yesNoNav(
          ua = ua,
          fromPage = AddressYesNoPage(index),
          yesCall = rts.AddressUKYesNoController.onPageLoad(index, draftId),
          noCall = rts.AnswersController.onPageLoad(index, draftId)
        )
      )
    case CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceYesNoPage(index),
        yesCall = nonTaxRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = trustTaxableYesNo(draftId, index)(ua)
      )
    case CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = CountryOfResidenceInTheUkYesNoPage(index),
        yesCall = trustTaxableYesNo(draftId, index)(ua),
        noCall = nonTaxRts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateAwayFromShareOfIncomeQuestions(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      nonTaxRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      rts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  private def trustTaxableYesNo(draftId: String, index: Int): PartialFunction[ReadableUserAnswers, Call] = {
    case ua =>
      if (is5mldNonTaxable(ua)) {
        rts.AnswersController.onPageLoad(index, draftId)
      } else {
        rts.AddressYesNoController.onPageLoad(index, draftId)
      }
  }

  private def is5mldNonTaxable(ua: ReadableUserAnswers): Boolean = {
    ua.is5mldEnabled && !ua.isTaxable
  }

}

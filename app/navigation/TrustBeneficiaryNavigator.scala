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

import controllers.register.beneficiaries.charityortrust.trust.{routes => rts}
import pages.register.beneficiaries.trust._
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, ReadableUserAnswers}
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class TrustBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(mode, draftId)(page)(userAnswers)

  private def simpleNavigation(mode: Mode, draftId: String): PartialFunction[Page, Call] = {
    case NamePage(index) => rts.DiscretionYesNoController.onPageLoad(mode, index, draftId)
    case ShareOfIncomePage(index) => rts.AddressYesNoController.onPageLoad(mode, index, draftId)
  }

  private def yesNoNavigation(mode: Mode, draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DiscretionYesNoPage(index) => ua =>
      yesNoNav(ua, DiscretionYesNoPage(index), rts.AddressYesNoController.onPageLoad(mode, index, draftId), rts.ShareOfIncomeController.onPageLoad(mode, index, draftId))
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(ua, AddressUKYesNoPage(index), rts.AddressUKController.onPageLoad(mode, index, draftId), rts.AddressInternationalController.onPageLoad(mode, index, draftId))
  }

  private def navigationWithCheck(mode: Mode, draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    mode match {
      case NormalMode => {
        case AddressYesNoPage(index) => ua =>
          yesNoNav(
            ua,
            AddressYesNoPage(index), rts.AddressUKYesNoController.onPageLoad(mode, index, draftId),
            yesNoNav(
              ua,
              AddressYesNoPage(index), rts.AddressUKYesNoController.onPageLoad(mode, index, draftId), ???))
      }
      case CheckMode => {
        case _:AddressUKPage | _:AddressInternationalPage => ua =>
          ???
        case AddressYesNoPage(index) => ua =>
          yesNoNav(
            ua,
            AddressYesNoPage(index),
            rts.AddressUKYesNoController.onPageLoad(mode, index, draftId),
            ???
          )
        }
    }
  }

  def yesNoNav(ua: ReadableUserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def routes(mode: Mode, draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(mode, draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      yesNoNavigation(mode, draftId) orElse
      navigationWithCheck(mode, draftId)

}

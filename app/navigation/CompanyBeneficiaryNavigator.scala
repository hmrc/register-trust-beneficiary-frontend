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

import config.FrontendAppConfig
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => rts}
import javax.inject.Inject
import models.ReadableUserAnswers
import pages.register.beneficiaries.company._
import pages.{Page, QuestionPage}
import play.api.mvc.Call

class CompanyBeneficiaryNavigator @Inject()(frontendAppConfig: FrontendAppConfig) extends Navigator(frontendAppConfig) {

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case NamePage(index) => rts.DiscretionYesNoController.onPageLoad(index, draftId)
    case ShareOfIncomePage(index) => rts.AddressYesNoController.onPageLoad(index, draftId)
    case AddressUKPage(index) => rts.CheckDetailsController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => rts.CheckDetailsController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        DiscretionYesNoPage(index),
        rts.AddressYesNoController.onPageLoad(index, draftId),
        rts.ShareOfIncomeController.onPageLoad(index, draftId))
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        rts.AddressUkYesNoController.onPageLoad(index, draftId),
        rts.CheckDetailsController.onPageLoad(index, draftId))
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUKYesNoPage(index),
        rts.UkAddressController.onPageLoad(index, draftId),
        rts.NonUkAddressController.onPageLoad(index, draftId))
  }

  def yesNoNav(ua: ReadableUserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  override def route(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      yesNoNavigation(draftId)
  }

}

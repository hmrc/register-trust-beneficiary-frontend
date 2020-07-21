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
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, ReadableUserAnswers, UserAnswers}
import pages.{Page, QuestionPage}
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

class CompanyBeneficiaryNavigator @Inject()(frontendAppConfig: FrontendAppConfig) extends Navigator(frontendAppConfig) {

//  override def nextPage(page: Page, mode: Mode, draftId: String, af :AffinityGroup = AffinityGroup.Organisation): ReadableUserAnswers => Call = mode match {
//    case NormalMode =>
//      route(draftId)(page)(af)
//    case CheckMode =>
//      route(draftId)(page)(af)
//  }

//  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
//    case NamePage => rts.DiscretionYesNoController.onPageLoad(mode)
//    case ShareOfIncomePage => rts.AddressYesNoController.onPageLoad(mode)
//    case StartDatePage => rts.CheckDetailsController.onPageLoad()
//  }
//
//  private def yesNoNavigation(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
//    case DiscretionYesNoPage => ua =>
//      yesNoNav(ua, DiscretionYesNoPage, rts.AddressYesNoController.onPageLoad(mode), rts.ShareOfIncomeController.onPageLoad(mode))
//    case AddressUkYesNoPage => ua =>
//      yesNoNav(ua, AddressUkYesNoPage, rts.UkAddressController.onPageLoad(mode), rts.NonUkAddressController.onPageLoad(mode))
//  }
//
//  private def navigationWithCheck(mode: Mode) : PartialFunction[Page, UserAnswers => Call] = {
//    mode match {
//      case NormalMode => {
//        case UkAddressPage | NonUkAddressPage => _ =>
//          rts.StartDateController.onPageLoad()
//        case AddressYesNoPage => ua =>
//          yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), rts.StartDateController.onPageLoad()))
//      }
//      case CheckMode => {
//        case UkAddressPage | NonUkAddressPage => ua =>
//          checkDetailsRoute(ua)
//        case AddressYesNoPage => ua =>
//          yesNoNav(ua, AddressYesNoPage, rts.AddressUkYesNoController.onPageLoad(mode), checkDetailsRoute(ua))
//      }
//    }
//  }
//
//  def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
//    ua.get(fromPage)
//      .map(if (_) yesCall else noCall)
//      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
//  }
//
//  def checkDetailsRoute(answers: UserAnswers) : Call = {
//    answers.get(IndexPage) match {
//      case None => controllers.routes.SessionExpiredController.onPageLoad()
//      case Some(x) =>
//        controllers.companyoremploymentrelated.company.amend.routes.CheckDetailsController.renderFromUserAnswers(x)
//    }
//  }

  override def route(draftId: String): PartialFunction[Page, AffinityGroup => ReadableUserAnswers => Call] = {
    case _ => _ => _ => controllers.routes.IndexController.onPageLoad(draftId)
  }

}

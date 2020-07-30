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

import javax.inject.Inject
import models.ReadableUserAnswers
//import pages.register.beneficiaries.other._
import pages.Page
import play.api.mvc.Call

class OtherBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(page)(userAnswers)

  private val simpleNavigation: PartialFunction[Page, Call] =
  {
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
//    case DescriptionPage(index) => rts.DiscretionYesNoController.onPageLoad()
//    case ShareOfIncomePage(index) => rts.AddressYesNoController.onPageLoad()
//    case UkAddressPage => rts.StartDateController.onPageLoad()
//    case NonUkAddressPage => rts.StartDateController.onPageLoad()
  }

//  private val yesNoNavigation : PartialFunction[Page, ReadableUserAnswers => Call] =
//  {
//    case IncomeYesNoPage(index) => ua =>
//      yesNoNav(ua, IncomeYesNoPage(index), rts.AddressYesNoController.onPageLoad(), rts.ShareOfIncomeController.onPageLoad())
//    case AddressYesNoPage(index) => ua =>
//      yesNoNav(ua, AddressYesNoPage(index), rts.AddressUkYesNoController.onPageLoad(), rts.StartDateController.onPageLoad())
//    case AddressUKYesNoPage(index) => ua =>
//      yesNoNav(ua, AddressUKYesNoPage(index), rts.UkAddressController.onPageLoad(), rts.NonUkAddressController.onPageLoad())
//  }

  val routes: PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation andThen (c => (_:ReadableUserAnswers) => c)
//  orElse yesNoNavigation

}

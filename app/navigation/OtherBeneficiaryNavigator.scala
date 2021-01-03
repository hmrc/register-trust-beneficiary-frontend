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

import javax.inject.Inject
import models.ReadableUserAnswers
import pages.register.beneficiaries.other._
import pages.Page
import play.api.mvc.Call
import controllers.register.beneficiaries.other

class OtherBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] =
  {
    case DescriptionPage(index) => other.routes.DiscretionYesNoController.onPageLoad(index, draftId)
    case ShareOfIncomePage(index) => other.routes.AddressYesNoController.onPageLoad(index, draftId)
    case AddressUKPage(index) => other.routes.CheckDetailsController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => other.routes.CheckDetailsController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] =
  {
    case IncomeDiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        IncomeDiscretionYesNoPage(index),
        other.routes.AddressYesNoController.onPageLoad(index, draftId),
        other.routes.ShareOfIncomeController.onPageLoad(index, draftId))
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        other.routes.AddressUkYesNoController.onPageLoad(index, draftId),
        other.routes.CheckDetailsController.onPageLoad(index, draftId))
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUKYesNoPage(index),
        other.routes.UkAddressController.onPageLoad(index, draftId),
        other.routes.NonUkAddressController.onPageLoad(index, draftId))
  }

  def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
    yesNoNavigation(draftId)

}

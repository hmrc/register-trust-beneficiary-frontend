/*
 * Copyright 2026 HM Revenue & Customs
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

import controllers.register.beneficiaries.other
import javax.inject.Inject
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.other.mld5.{
  CountryOfResidencePage, CountryOfResidenceYesNoPage, UKResidentYesNoPage
}
import play.api.mvc.Call

class OtherBeneficiaryNavigator @Inject() () extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DescriptionPage(index)          =>
      ua =>
        if (isNonTaxable(ua)) {
          other.mld5.routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
        } else {
          other.routes.DiscretionYesNoController.onPageLoad(index, draftId)
        }
    case ShareOfIncomePage(index)        =>
      _ =>
        controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceYesNoController
          .onPageLoad(index, draftId)
    case AddressUKPage(index)            => _ => other.routes.CheckDetailsController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => _ => other.routes.CheckDetailsController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index)   =>
      ua =>
        if (isNonTaxable(ua)) {
          other.routes.CheckDetailsController.onPageLoad(index, draftId)
        } else {
          other.routes.AddressYesNoController.onPageLoad(index, draftId)
        }
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ IncomeDiscretionYesNoPage(index)   =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceYesNoController
            .onPageLoad(index, draftId),
          noCall = other.routes.ShareOfIncomeController.onPageLoad(index, draftId)
        )
    case page @ AddressYesNoPage(index)            =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = other.routes.AddressUkYesNoController.onPageLoad(index, draftId),
          noCall = other.routes.CheckDetailsController.onPageLoad(index, draftId)
        )
    case page @ AddressUKYesNoPage(index)          =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = other.routes.UkAddressController.onPageLoad(index, draftId),
          noCall = other.routes.NonUkAddressController.onPageLoad(index, draftId)
        )
    case page @ CountryOfResidenceYesNoPage(index) =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = other.mld5.routes.UKResidentYesNoController.onPageLoad(index, draftId),
          noCall = navigateToCheckDetailsOrAddressQuestions(draftId, index)(ua)
        )
    case page @ UKResidentYesNoPage(index)         =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = navigateToCheckDetailsOrAddressQuestions(draftId, index)(ua),
          noCall = other.mld5.routes.CountryOfResidenceController.onPageLoad(index, draftId)
        )
  }

  private def navigateToCheckDetailsOrAddressQuestions(
    draftId: String,
    index: Int
  ): PartialFunction[ReadableUserAnswers, Call] = { case ua =>
    if (isNonTaxable(ua)) {
      other.routes.CheckDetailsController.onPageLoad(index, draftId)
    } else {
      other.routes.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

}

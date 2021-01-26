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

import controllers.register.beneficiaries.companyoremploymentrelated.company.mld5.{routes => ntrts}
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.companyoremploymentrelated.company._
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5._
import play.api.mvc.Call

class CompanyBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    simpleNavigation(draftId) orElse yesNoNavigation(draftId)
  }

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => rts.DiscretionYesNoController.onPageLoad(index, draftId)
    case IncomePage(index) => ua =>  navigateAwayFromShareOfIncomeQuestions(draftId, index, ua.is5mldEnabled)
    case AddressUKPage(index) => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => _ => rts.AddressYesNoController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ IncomeYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromShareOfIncomeQuestions(draftId, index, ua.is5mldEnabled),
        noCall = rts.ShareOfIncomeController.onPageLoad(index, draftId)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.AddressUkYesNoController.onPageLoad(index, draftId),
        noCall = rts.CheckDetailsController.onPageLoad(index, draftId))
    case page @ AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.UkAddressController.onPageLoad(index, draftId),
        noCall = rts.NonUkAddressController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = ntrts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = rts.AddressYesNoController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.AddressYesNoController.onPageLoad(index, draftId),
        noCall = ntrts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateAwayFromShareOfIncomeQuestions(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      ntrts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      rts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

}

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

import com.google.inject.Inject
import controllers.register.beneficiaries.charityortrust.trust.nonTaxable.{routes => nonTaxRts}
import controllers.register.beneficiaries.charityortrust.trust.{routes => rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.charityortrust.trust._
import pages.register.beneficiaries.charityortrust.trust.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.mvc.Call

class TrustBeneficiaryNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    nextPage(page, draftId, false, userAnswers)

  override def nextPage(page: Page, draftId: String, fiveMld: Boolean, userAnswers: ReadableUserAnswers): Call =
    routes(draftId, fiveMld)(page)(userAnswers)

  private def simpleNavigation(draftId: String, fiveMld: Boolean): PartialFunction[Page, Call] = {
    case NamePage(index) => rts.DiscretionYesNoController.onPageLoad(index, draftId)
    case ShareOfIncomePage(index) => fiveMldYesNo(draftId, index, fiveMld)
    case AddressUKPage(index) => rts.AnswersController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => rts.AnswersController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => rts.AddressYesNoController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String, fiveMld: Boolean): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DiscretionYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        DiscretionYesNoPage(index),
        fiveMldYesNo(draftId, index, fiveMld),
        rts.ShareOfIncomeController.onPageLoad(index, draftId)
      )
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUKYesNoPage(index),
        rts.AddressUKController.onPageLoad(index, draftId),
        rts.AddressInternationalController.onPageLoad(index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index), rts.AddressUKYesNoController.onPageLoad(index, draftId),
        yesNoNav(
          ua,
          AddressYesNoPage(index), rts.AddressUKYesNoController.onPageLoad(index, draftId),
          rts.AnswersController.onPageLoad(index, draftId)
      ))
    case CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        CountryOfResidenceYesNoPage(index),
        nonTaxRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        rts.AddressYesNoController.onPageLoad(index, draftId)
      )
    case CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        CountryOfResidenceInTheUkYesNoPage(index),
        rts.AddressYesNoController.onPageLoad(index, draftId),
        nonTaxRts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def fiveMldYesNo(draftId: String, index: Int, fiveMld: Boolean): Call = {
    if (fiveMld) {
      nonTaxRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      rts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  def routes(draftId: String, fiveMld: Boolean): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId, fiveMld) andThen (c => (_: ReadableUserAnswers) => c) orElse yesNoNavigation(draftId, fiveMld)

}

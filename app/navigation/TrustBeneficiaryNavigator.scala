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
import pages.register.beneficiaries.charityortrust.trust._
import pages.register.beneficiaries.charityortrust.trust.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.mvc.Call

class TrustBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    nextPage(page, draftId, fiveMldEnabled = false, trustTaxable = true, userAnswers)

  override def nextPage(page: Page, draftId: String, fiveMldEnabled: Boolean, trustTaxable: Boolean, userAnswers: ReadableUserAnswers): Call =
    routes(draftId, fiveMldEnabled, trustTaxable)(page)(userAnswers)

  private def simpleNavigation(draftId: String, fiveMldEnabled: Boolean, trustTaxable: Boolean): PartialFunction[Page, Call] = {
    if (fiveMldEnabled && !trustTaxable) {
      case NamePage(index) => nonTaxRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
      case CountryOfResidencePage(index) => rts.AnswersController.onPageLoad(index, draftId)
    } else {
      case NamePage(index) => rts.DiscretionYesNoController.onPageLoad(index, draftId)
      case ShareOfIncomePage(index) => fiveMldYesNo(draftId, index, fiveMldEnabled)
      case AddressUKPage(index) => rts.AnswersController.onPageLoad(index, draftId)
      case AddressInternationalPage(index) => rts.AnswersController.onPageLoad(index, draftId)
      case CountryOfResidencePage(index) => rts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  private def yesNoNavigation(draftId: String, fiveMldEnabled: Boolean, trustTaxable: Boolean): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DiscretionYesNoPage(index) => ua =>
      yesNoNav(ua, DiscretionYesNoPage(index), fiveMldYesNo(draftId, index, fiveMldEnabled), rts.ShareOfIncomeController.onPageLoad(index, draftId))
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUKYesNoPage(index),
        rts.AddressUKController.onPageLoad(index, draftId),
        rts.AddressInternationalController.onPageLoad(index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(ua, AddressYesNoPage(index), rts.AddressUKYesNoController.onPageLoad(index, draftId), yesNoNav(
        ua,
        AddressYesNoPage(index), rts.AddressUKYesNoController.onPageLoad(index, draftId),
        rts.AnswersController.onPageLoad(index, draftId)
      ))
    case CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        CountryOfResidenceYesNoPage(index),
        nonTaxRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        trustTaxableYesNo(draftId, index, fiveMldEnabled, trustTaxable)
      )
    case CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        CountryOfResidenceInTheUkYesNoPage(index),
        trustTaxableYesNo(draftId, index, fiveMldEnabled, trustTaxable),
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

  private def trustTaxableYesNo(draftId: String, index: Int, fiveMld: Boolean, trustTaxable: Boolean): Call = {
    if (fiveMld && !trustTaxable) {
      rts.AnswersController.onPageLoad(index, draftId)
    } else {
      rts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  def routes(draftId: String, fiveMld: Boolean, trustTaxable: Boolean): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId, fiveMld, trustTaxable) andThen (c => (_: ReadableUserAnswers) => c) orElse yesNoNavigation(draftId, fiveMld, trustTaxable)

}

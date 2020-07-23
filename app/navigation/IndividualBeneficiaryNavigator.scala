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

import controllers.register.beneficiaries.individualBeneficiary.routes._
import models.registration.pages.KindOfTrust.Employees
import models.{NormalMode, ReadableUserAnswers}
import pages.Page
import pages.register.beneficiaries.individual._
import pages.register.settlors.living_settlor.trust_type.KindOfTrustPage
import play.api.mvc.Call

class IndividualBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case RoleInCompanyPage(index) => DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case DateOfBirthPage(index) => IncomeYesNoController.onPageLoad(NormalMode, index, draftId)
    case IncomePage(index) => NationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
    case NationalInsuranceNumberPage(index) => VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case AddressUKPage(index) => PassportDetailsYesNoController.onPageLoad(NormalMode, index, draftId)
    case AddressInternationalPage(index) => PassportDetailsYesNoController.onPageLoad(NormalMode, index, draftId)
    case PassportDetailsPage(index) => VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case IDCardDetailsPage(index) => VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case VulnerableYesNoPage(index) => AnswersController.onPageLoad(index, draftId)
    case AnswersPage => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        DateOfBirthYesNoPage(index),
        DateOfBirthController.onPageLoad(NormalMode, index, draftId),
        IncomeYesNoController.onPageLoad(NormalMode, index, draftId)
      )
    case IncomeYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        IncomeYesNoPage(index),
        NationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId),
        IncomeController.onPageLoad(NormalMode, index, draftId)
      )
    case NationalInsuranceYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        NationalInsuranceYesNoPage(index),
        NationalInsuranceNumberController.onPageLoad(NormalMode, index, draftId),
        AddressYesNoController.onPageLoad(NormalMode, index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        AddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
        VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
      )
    case AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUKYesNoPage(index),
        AddressUKController.onPageLoad(NormalMode, index, draftId),
        AddressInternationalController.onPageLoad(NormalMode, index, draftId)
      )
    case PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        PassportDetailsYesNoPage(index),
        PassportDetailsController.onPageLoad(NormalMode, index, draftId),
        IDCardDetailsYesNoController.onPageLoad(NormalMode, index, draftId)
      )
    case IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        IDCardDetailsYesNoPage(index),
        IDCardDetailsController.onPageLoad(NormalMode, index, draftId),
        VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
      )
  }

  private def trustTypeNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => ua => ua.get(KindOfTrustPage) match {
      case Some(Employees) => RoleInCompanyController.onPageLoad(NormalMode, index, draftId)
      case _ => DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    }
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      conditionalNavigation(draftId) orElse
      trustTypeNavigation(draftId)
}

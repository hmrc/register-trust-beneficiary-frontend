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

import controllers.register.beneficiaries.individualBeneficiary.mld5.routes._
import controllers.register.beneficiaries.individualBeneficiary.routes._
import models.ReadableUserAnswers
import models.registration.pages.KindOfTrust.Employees
import pages.Page
import pages.register.KindOfTrustPage
import pages.register.beneficiaries.AnswersPage
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.individual.mld5._
import play.api.mvc.Call

class IndividualBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      conditionalNavigation(draftId) orElse
      trustTypeNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case RoleInCompanyPage(index) => _ => DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => ua => navigateAwayFromDateOfBirthPages(draftId, index, ua)
    case IncomePage(index) => _ => CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    case CountryOfNationalityPage(index) => ua => navigateAwayFromNationalityPages(draftId, index, ua)
    case NationalInsuranceNumberPage(index) => _ => CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => ua => navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua)
    case AddressUKPage(index) => _ => PassportDetailsYesNoController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => _ => PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => _ => MentalCapacityYesNoController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => _ => MentalCapacityYesNoController.onPageLoad(index, draftId)
    case MentalCapacityYesNoPage(index) => ua => navigateAwayFromMentalCapacityPage(draftId, index, ua)
    case VulnerableYesNoPage(index) => _ => AnswersController.onPageLoad(index, draftId)
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = DateOfBirthController.onPageLoad(index, draftId),
        noCall = navigateAwayFromDateOfBirthPages(draftId, index, ua)
      )
    case page @ IncomeYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfNationalityYesNoController.onPageLoad(index, draftId),
        noCall = IncomeController.onPageLoad(index, draftId)
      )
    case page @ CountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromNationalityPages(draftId, index, ua)
      )
    case page @ CountryOfNationalityInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromNationalityPages(draftId, index, ua),
        noCall = CountryOfNationalityController.onPageLoad(index, draftId)
      )
    case page @ NationalInsuranceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = NationalInsuranceNumberController.onPageLoad(index, draftId),
        noCall = CountryOfResidenceYesNoController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua),
        noCall = CountryOfResidenceController.onPageLoad(index, draftId)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressUKYesNoController.onPageLoad(index, draftId),
        noCall = MentalCapacityYesNoController.onPageLoad(index, draftId)
      )
    case page @ AddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressUKController.onPageLoad(index, draftId),
        noCall = AddressInternationalController.onPageLoad(index, draftId)
      )
    case page @ PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = PassportDetailsController.onPageLoad(index, draftId),
        noCall = IDCardDetailsYesNoController.onPageLoad(index, draftId)
      )
    case page @ IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = IDCardDetailsController.onPageLoad(index, draftId),
        noCall = MentalCapacityYesNoController.onPageLoad(index, draftId)
      )
  }

  private def trustTypeNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => ua => (ua.get(KindOfTrustPage), ua.isTaxable) match {
      case (Some(Employees), true) => RoleInCompanyController.onPageLoad(index, draftId)
      case _ => DateOfBirthYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromDateOfBirthPages(draftId: String, index: Int, answers: ReadableUserAnswers): Call = {
    if (answers.isTaxable) {
      IncomeYesNoController.onPageLoad(index, draftId)
    } else {
      CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNationalityPages(draftId: String, index: Int, answers: ReadableUserAnswers): Call = {
    if (answers.isTaxable) {
      NationalInsuranceYesNoController.onPageLoad(index, draftId)
    } else {
      CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(draftId: String, index: Int, ua: ReadableUserAnswers): Call = {
    (ua.get(NationalInsuranceYesNoPage(index)), ua.isTaxable) match {
      case (Some(true), _) => MentalCapacityYesNoController.onPageLoad(index, draftId)
      case (_, true) => AddressYesNoController.onPageLoad(index, draftId)
      case _ => MentalCapacityYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromMentalCapacityPage(draftId: String, index: Int, ua: ReadableUserAnswers): Call = {
    if (ua.isTaxable) {
      VulnerableYesNoController.onPageLoad(index, draftId)
    } else {
      AnswersController.onPageLoad(index, draftId)
    }
  }

}

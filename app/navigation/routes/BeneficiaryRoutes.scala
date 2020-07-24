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

package navigation.routes

import config.FrontendAppConfig
import controllers.register.beneficiaries.charityOrTrust.charity.{routes => charityRoutes}
import controllers.register.beneficiaries.charityOrTrust.{routes => charityOrTrustRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import mapping.reads.CharityBeneficiary
import models.registration.pages.CharityOrTrust.{Charity, Trust}
import models.registration.pages.KindOfTrust.Employees
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{NormalMode, ReadableUserAnswers}
import pages.Page
import pages.register.KindOfTrustPage
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, charity}
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage, _}
import play.api.mvc.Call
import sections.beneficiaries.{CharityBeneficiaries, ClassOfBeneficiaries, IndividualBeneficiaries}

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => namePage(draftId, index)
    case RoleInCompanyPage(index) => _ => individualRoutes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case DateOfBirthYesNoPage(index) => ua => individualBeneficiaryDateOfBirthRoute(ua, index, draftId)
    case DateOfBirthPage(index) => _ => individualRoutes.IncomeYesNoController.onPageLoad(NormalMode, index, draftId)
    case IncomeYesNoPage(index) => ua => individualBeneficiaryIncomeRoute(ua, index, draftId)
    case IncomePage(index) => _ => individualRoutes.NationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
    case NationalInsuranceYesNoPage(index) => ua => individualBeneficiaryNationalInsuranceYesNoRoute(ua, index, draftId)
    case NationalInsuranceNumberPage(index) => _ => individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case pages.register.beneficiaries.individual.AddressYesNoPage(index) => ua => individualBeneficiaryAddressRoute(ua, index, draftId)
    case AddressUKYesNoPage(index) => ua => individualBeneficiaryAddressUKYesNoRoute(ua, index, draftId)
    case AddressUKPage(index) => _ => individualRoutes.PassportDetailsYesNoController.onPageLoad(NormalMode, index, draftId)
    case AddressInternationalPage(index) => _ => individualRoutes.PassportDetailsYesNoController.onPageLoad(NormalMode, index, draftId)
    case PassportDetailsYesNoPage(index) => ua => individualBeneficiaryPassportDetailsYesNoRoute(ua, index, draftId)
    case PassportDetailsPage(index) => _ => individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case IDCardDetailsYesNoPage(index) => ua => individualBeneficiaryIdCardDetailsYesNoRoute(ua, index, draftId)
    case IDCardDetailsPage(index) => _ => individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case VulnerableYesNoPage(index) => _ => individualRoutes.AnswersController.onPageLoad(index, draftId)
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case AddABeneficiaryPage => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => whatTypeOfBeneficiaryRoute(draftId)
    case ClassBeneficiaryDescriptionPage(_) => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case CharityOrTrustPage => charityOrTrust(draftId)
    case CharityNamePage(index) => _ => charityRoutes.AmountDiscretionYesNoController.onPageLoad(NormalMode, index,draftId)
    case AmountDiscretionYesNoPage(index) =>  amountDiscretionYesNoRoute(draftId, index)
    case HowMuchIncomePage(index) => _ =>  charityRoutes.AddressYesNoController.onPageLoad(NormalMode, index,draftId)
    case charity.AddressYesNoPage(index) =>  addressYesNoRoute(draftId, index)
    case AddressInTheUkYesNoPage(index) =>  addressInTheUKYesNoRoute(draftId, index)
    case CharityAddressUKPage(index) => _ => charityRoutes.CharityAnswersController.onPageLoad(index, draftId)
    case CharityInternationalAddressPage(index) => _ =>  charityRoutes.CharityAnswersController.onPageLoad(index, draftId)
  }

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig) : Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def addABeneficiaryRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers) = {
    val addAnother = answers.get(AddABeneficiaryPage)
    addAnother match {
      case Some(AddABeneficiary.YesNow) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(AddABeneficiary.YesLater) => assetsCompletedRoute(draftId, config)
      case Some(AddABeneficiary.NoComplete) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call = {
    val whatBeneficiaryToAdd = userAnswers.get(WhatTypeOfBeneficiaryPage)
    whatBeneficiaryToAdd match {
      case Some(WhatTypeOfBeneficiary.Individual) =>
        routeToIndividualBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.ClassOfBeneficiary) =>
        routeToClassOfBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.CharityOrTrust) =>
        charityOrTrustRoutes.CharityOrTrustController.onPageLoad(NormalMode, draftId)
      case _ =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
    }
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case Nil =>
        individualRoutes.NameController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        individualRoutes.NameController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def routeToCharityIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val charityOrTrust = userAnswers.get(CharityBeneficiaries).getOrElse(List.empty)
    charityOrTrust match {
      case Nil =>
        controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityNameController.onPageLoad(NormalMode, 0  ,draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityNameController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def individualBeneficiaryAddressRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(individual.AddressYesNoPage(index)) match {
      case Some(false) => individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.AddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressUKYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(AddressUKYesNoPage(index)) match {
      case Some(false) => individualRoutes.AddressInternationalController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.AddressUKController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryPassportDetailsYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(PassportDetailsYesNoPage(index)) match {
      case Some(false) => individualRoutes.IDCardDetailsYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.PassportDetailsController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryIdCardDetailsYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IDCardDetailsYesNoPage(index)) match {
      case Some(false) => individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.IDCardDetailsController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryNationalInsuranceYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(NationalInsuranceYesNoPage(index)) match {
      case Some(false) => individualRoutes.AddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.NationalInsuranceNumberController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryIncomeRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IncomeYesNoPage(index)) match {
      case Some(false) => individualRoutes.IncomeController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.NationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryDateOfBirthRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(DateOfBirthYesNoPage(index)) match {
      case Some(false) => individualRoutes.IncomeYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => individualRoutes.DateOfBirthController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def addABeneficiaryYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers) = {
    val add = answers.get(AddABeneficiaryYesNoPage)

    add match {
      case Some(true) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(false) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def namePage(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(KindOfTrustPage) match {
    case Some(Employees) => individualRoutes.RoleInCompanyController.onPageLoad(NormalMode, index, draftId)
    case _ => individualRoutes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
  }

  private def charityOrTrust(draftId: String)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(CharityOrTrustPage) match {
    case Some(Charity) => routeToCharityIndex(userAnswers, draftId)
    case Some(Trust) => controllers.routes.FeatureNotAvailableController.onPageLoad()
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def amountDiscretionYesNoRoute(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(AmountDiscretionYesNoPage(index)) match {
    case Some(true) => charityRoutes.AddressYesNoController.onPageLoad(NormalMode, index, draftId)
    case Some(false) => charityRoutes.HowMuchIncomeController.onPageLoad(NormalMode, index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def addressYesNoRoute(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(charity.AddressYesNoPage(index)) match {
    case Some(false) => charityRoutes.CharityAnswersController.onPageLoad(index, draftId)
    case Some(true) => charityRoutes.AddressInTheUkYesNoController.onPageLoad(NormalMode, index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def addressInTheUKYesNoRoute(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(AddressInTheUkYesNoPage(index)) match {
    case Some(false) => charityRoutes.CharityInternationalAddressController.onPageLoad(NormalMode, index, draftId)
    case Some(true) => charityRoutes.CharityAddressUKController.onPageLoad(NormalMode, index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }
}


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
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import models.registration.pages.CharityOrTrust.{Charity, Trust}
import models.registration.pages.KindOfTrust.Employees
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{NormalMode, ReadableUserAnswers}
import pages.Page
import pages.register.KindOfTrustPage
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, charity}
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, WhatTypeOfBeneficiaryPage, _}
import play.api.mvc.Call
import sections.beneficiaries.{CharityBeneficiaries, ClassOfBeneficiaries, IndividualBeneficiaries}

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => namePage(draftId, index)
    case RoleInCompanyPage(index) => _ => individualRoutes.DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthYesNoPage(index) => ua => individualBeneficiaryDateOfBirthRoute(ua, index, draftId)
    case DateOfBirthPage(index) => _ => individualRoutes.IncomeYesNoController.onPageLoad(index, draftId)
    case IncomeYesNoPage(index) => ua => individualBeneficiaryIncomeRoute(ua, index, draftId)
    case IncomePage(index) => _ => individualRoutes.NationalInsuranceYesNoController.onPageLoad(index, draftId)
    case NationalInsuranceYesNoPage(index) => ua => individualBeneficiaryNationalInsuranceYesNoRoute(ua, index, draftId)
    case NationalInsuranceNumberPage(index) => _ => individualRoutes.VulnerableYesNoController.onPageLoad(index, draftId)
    case pages.register.beneficiaries.individual.AddressYesNoPage(index) => ua => individualBeneficiaryAddressRoute(ua, index, draftId)
    case AddressUKYesNoPage(index) => ua => individualBeneficiaryAddressUKYesNoRoute(ua, index, draftId)
    case AddressUKPage(index) => _ => individualRoutes.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case AddressInternationalPage(index) => _ => individualRoutes.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsYesNoPage(index) => ua => individualBeneficiaryPassportDetailsYesNoRoute(ua, index, draftId)
    case PassportDetailsPage(index) => _ => individualRoutes.VulnerableYesNoController.onPageLoad(index, draftId)
    case IDCardDetailsYesNoPage(index) => ua => individualBeneficiaryIdCardDetailsYesNoRoute(ua, index, draftId)
    case IDCardDetailsPage(index) => _ => individualRoutes.VulnerableYesNoController.onPageLoad(index, draftId)
    case VulnerableYesNoPage(index) => _ => individualRoutes.AnswersController.onPageLoad(index, draftId)
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case AddABeneficiaryPage => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => whatTypeOfBeneficiaryRoute(draftId)
    case ClassBeneficiaryDescriptionPage(_) => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case CharityOrTrustPage => charityOrTrust(draftId)
    case CharityNamePage(index) => _ => controllers.register.beneficiaries.charityortrust.charity.routes.AmountDiscretionYesNoController.onPageLoad(index,draftId)
    case AmountDiscretionYesNoPage(index) =>  amountDiscretionYesNoRoute(draftId, index)
    case HowMuchIncomePage(index) => _ =>  controllers.register.beneficiaries.charityortrust.charity.routes.AddressYesNoController.onPageLoad(index,draftId)
    case charity.AddressYesNoPage(index) =>  addressYesNoRoute(draftId, index)
    case AddressInTheUkYesNoPage(index) =>  addressInTheUKYesNoRoute(draftId, index)
    case CharityAddressUKPage(index) => _ => controllers.register.beneficiaries.charityortrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId)
    case CharityInternationalAddressPage(index) => _ =>  controllers.register.beneficiaries.charityortrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId)
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
        controllers.register.beneficiaries.charityortrust.routes.CharityOrTrustController.onPageLoad(draftId)
      case _ =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
    }
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case Nil =>
        individualRoutes.NameController.onPageLoad(0, draftId)
      case t if t.nonEmpty =>
        individualRoutes.NameController.onPageLoad(t.size, draftId)
    }
  }

  private def routeToCharityIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val charityOrTrust = userAnswers.get(CharityBeneficiaries).getOrElse(List.empty)
    charityOrTrust match {
      case Nil =>
        controllers.register.beneficiaries.charityortrust.charity.routes.CharityNameController.onPageLoad(0  ,draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.charityortrust.charity.routes.CharityNameController.onPageLoad(t.size, draftId)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.classofbeneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.classofbeneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(t.size, draftId)
    }
  }

  private def individualBeneficiaryAddressRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(individual.AddressYesNoPage(index)) match {
      case Some(false) => individualRoutes.VulnerableYesNoController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.AddressUKYesNoController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressUKYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(AddressUKYesNoPage(index)) match {
      case Some(false) => individualRoutes.AddressInternationalController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.AddressUKController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryPassportDetailsYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(PassportDetailsYesNoPage(index)) match {
      case Some(false) => individualRoutes.IDCardDetailsYesNoController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.PassportDetailsController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryIdCardDetailsYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IDCardDetailsYesNoPage(index)) match {
      case Some(false) => individualRoutes.VulnerableYesNoController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.IDCardDetailsController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryNationalInsuranceYesNoRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(NationalInsuranceYesNoPage(index)) match {
      case Some(false) => individualRoutes.AddressYesNoController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.NationalInsuranceNumberController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryIncomeRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IncomeYesNoPage(index)) match {
      case Some(false) => individualRoutes.IncomeController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.NationalInsuranceYesNoController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryDateOfBirthRoute(userAnswers: ReadableUserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(DateOfBirthYesNoPage(index)) match {
      case Some(false) => individualRoutes.IncomeYesNoController.onPageLoad(index, draftId)
      case Some(true) => individualRoutes.DateOfBirthController.onPageLoad(index, draftId)
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
    case Some(Employees) => individualRoutes.RoleInCompanyController.onPageLoad(index, draftId)
    case _ => individualRoutes.DateOfBirthYesNoController.onPageLoad(index, draftId)
  }

  private def charityOrTrust(draftId: String)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(CharityOrTrustPage) match {
    case Some(Charity) => routeToCharityIndex(userAnswers, draftId)
    case Some(Trust) => controllers.routes.FeatureNotAvailableController.onPageLoad()
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def amountDiscretionYesNoRoute(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(AmountDiscretionYesNoPage(index)) match {
    case Some(true) => controllers.register.beneficiaries.charityortrust.charity.routes.AddressYesNoController.onPageLoad(index, draftId)
    case Some(false) => controllers.register.beneficiaries.charityortrust.charity.routes.HowMuchIncomeController.onPageLoad(index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def addressYesNoRoute(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(charity.AddressYesNoPage(index)) match {
    case Some(false) => controllers.register.beneficiaries.charityortrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId)
    case Some(true) => controllers.register.beneficiaries.charityortrust.charity.routes.AddressInTheUkYesNoController.onPageLoad(index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def addressInTheUKYesNoRoute(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(AddressInTheUkYesNoPage(index)) match {
    case Some(false) => controllers.register.beneficiaries.charityortrust.charity.routes.CharityInternationalAddressController.onPageLoad(index, draftId)
    case Some(true) => controllers.register.beneficiaries.charityortrust.charity.routes.CharityAddressUKController.onPageLoad(index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }
}


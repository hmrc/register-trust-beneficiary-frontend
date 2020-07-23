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
import controllers.register.beneficiaries.charityortrust.charity.{routes => charityRoutes}
import controllers.register.beneficiaries.charityortrust.{routes => charityortrustRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import models.registration.pages.CharityOrTrust.{Charity, Trust}
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{NormalMode, ReadableUserAnswers}
import pages.Page
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, charity}
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries._
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case AddABeneficiaryPage => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => whatTypeOfBeneficiaryRoute(draftId)
    case ClassBeneficiaryDescriptionPage(_) => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case CharityOrTrustPage => charityortrust(draftId, 0)
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
        routeToCharityOrTrustIndex(userAnswers, draftId)
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

  private def routeToCharityOrTrustIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val charityortrust = userAnswers.get(CharityOrTrustPage).getOrElse(List.empty)
    charityortrust match {
      case Nil =>
        charityortrustRoutes.CharityOrTrustController.onPageLoad(NormalMode, draftId)
      case _ =>
        charityortrustRoutes.CharityOrTrustController.onPageLoad(NormalMode, draftId)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.classofbeneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.classofbeneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(t.size, draftId)
    }
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

  private def charityortrust(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(CharityOrTrustPage) match {
    case Some(Charity) => charityRoutes.CharityNameController.onPageLoad(NormalMode, index, draftId)
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


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
import controllers.register.routes
import models.{NormalMode, UserAnswers}
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import pages.Page
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage}
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}
import uk.gov.hmrc.auth.core.AffinityGroup

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case IndividualBeneficiaryNamePage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryDateOfBirthYesNoPage(index) => _ => ua => individualBeneficiaryDateOfBirthRoute(ua, index, draftId)
    case IndividualBeneficiaryDateOfBirthPage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryIncomeYesNoPage(index) => _ => ua => individualBeneficiaryIncomeRoute(ua, index, draftId)
    case IndividualBeneficiaryIncomePage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryNationalInsuranceYesNoPage(index) => _ => ua => individualBeneficiaryNationalInsuranceYesNoRoute(ua, index, draftId)
    case IndividualBeneficiaryNationalInsuranceNumberPage(index) => _ => _ =>
      controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryAddressYesNoPage(index) => _ => ua => individualBeneficiaryAddressRoute(ua, index, draftId)
    case IndividualBeneficiaryAddressUKYesNoPage(index) => _ => ua => individualBeneficiaryAddressUKYesNoRoute(ua, index, draftId)
    case IndividualBeneficiaryAddressUKPage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case IndividualBeneficiaryVulnerableYesNoPage(index) => _ => _ => controllers.register.beneficiaries.routes.IndividualBeneficiaryAnswersController.onPageLoad(index, draftId)
    case IndividualBeneficiaryAnswersPage => _ => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)

    case AddABeneficiaryPage => _ => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => _ => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => _ => whatTypeOfBeneficiaryRoute(draftId)
    case ClassBeneficiaryDescriptionPage(index) => _ => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
  }

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig) : Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: UserAnswers) : Call = {
    val whatBeneficiaryToAdd = userAnswers.get(WhatTypeOfBeneficiaryPage)
    whatBeneficiaryToAdd match {
      case Some(WhatTypeOfBeneficiary.Individual) =>
        routeToIndividualBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.ClassOfBeneficiary) =>
        routeToClassOfBeneficiaryIndex(userAnswers, draftId)
      case _ =>
        controllers.routes.FeatureNotAvailableController.onPageLoad()
    }
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: UserAnswers, draftId: String) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: UserAnswers, draftId: String) = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case Nil =>
        controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, t.size, draftId)
    }
  }

  private def individualBeneficiaryAddressRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryAddressYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryVulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryAddressUKYesNoRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryAddressUKYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressUKController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryNationalInsuranceYesNoRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryNationalInsuranceYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryAddressYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceNumberController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryIncomeRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryIncomeYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryNationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualBeneficiaryDateOfBirthRoute(userAnswers: UserAnswers, index: Int, draftId: String) : Call =
    userAnswers.get(IndividualBeneficiaryDateOfBirthYesNoPage(index)) match {
      case Some(false) => controllers.register.beneficiaries.routes.IndividualBeneficiaryIncomeYesNoController.onPageLoad(NormalMode, index, draftId)
      case Some(true) => controllers.register.beneficiaries.routes.IndividualBeneficiaryDateOfBirthController.onPageLoad(NormalMode, index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def addABeneficiaryYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: UserAnswers) = {
    val add = answers.get(AddABeneficiaryYesNoPage)

    add match {
      case Some(true) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(false) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addABeneficiaryRoute(draftId: String, config: FrontendAppConfig)(answers: UserAnswers) = {
    val addAnother = answers.get(AddABeneficiaryPage)
    addAnother match {
      case Some(AddABeneficiary.YesNow) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(AddABeneficiary.YesLater) => assetsCompletedRoute(draftId, config)
      case Some(AddABeneficiary.NoComplete) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}


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
import models.registration.pages.KindOfTrust.Employees
import models.{NormalMode, ReadableUserAnswers}
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import pages.Page
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage}
import pages.register.settlors.living_settlor.trust_type.KindOfTrustPage
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}
import uk.gov.hmrc.auth.core.AffinityGroup

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, AffinityGroup => ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => namePage(draftId, index)
    case RoleInCompanyPage(index) => _ => _ => individualRoutes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case DateOfBirthYesNoPage(index) => _ => ua => individualBeneficiaryDateOfBirthRoute(ua, index, draftId)
    case DateOfBirthPage(index) => _ => _ => individualRoutes.IncomeYesNoController.onPageLoad(NormalMode, index, draftId)
    case IncomeYesNoPage(index) => _ => ua => individualBeneficiaryIncomeRoute(ua, index, draftId)
    case IncomePage(index) => _ => _ => individualRoutes.NationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId)
    case NationalInsuranceYesNoPage(index) => _ => ua => individualBeneficiaryNationalInsuranceYesNoRoute(ua, index, draftId)
    case NationalInsuranceNumberPage(index) => _ =>_ =>
      individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case AddressYesNoPage(index) => _ => ua => individualBeneficiaryAddressRoute(ua, index, draftId)
    case AddressUKYesNoPage(index) => _ => ua => individualBeneficiaryAddressUKYesNoRoute(ua, index, draftId)
    case AddressUKPage(index) => _ => _ => individualRoutes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId)
    case VulnerableYesNoPage(index) => _ => _ => individualRoutes.AnswersController.onPageLoad(index, draftId)
    case AnswersPage => _ => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case AddABeneficiaryPage => _ => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => _ => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => _ => whatTypeOfBeneficiaryRoute(draftId)
    case ClassBeneficiaryDescriptionPage(_) => _ => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
  }

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig) : Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def namePage(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(KindOfTrustPage) match {
    case Some(Employees) => individualRoutes.RoleInCompanyController.onPageLoad(NormalMode, index, draftId)
    case _ => individualRoutes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call = {
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

  private def routeToIndividualBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case Nil =>
        individualRoutes.NameController.onPageLoad(NormalMode, 0, draftId)
      case t if t.nonEmpty =>
        individualRoutes.NameController.onPageLoad(NormalMode, t.size, draftId)
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
    userAnswers.get(AddressYesNoPage(index)) match {
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
}


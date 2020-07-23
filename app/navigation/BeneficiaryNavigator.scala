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

import config.FrontendAppConfig
import controllers.register.beneficiaries.classofbeneficiaries.{routes => classOfBeneficiariesRts}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRts}
import controllers.register.beneficiaries.charityortrust.{routes => charityortrustRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => companyRoutes}
import controllers.register.beneficiaries.charityortrust.charity.{routes => charityRoutes}
import controllers.register.beneficiaries.charityortrust.trust.{routes => trustRoutes}
import javax.inject.Inject
import models.CompanyOrEmploymentRelatedToAdd.Company
import models.registration.pages.CharityOrTrust.{Charity, Trust}
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{Mode, NormalMode, ReadableUserAnswers}
import pages.Page
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, AnswersPage, CompanyOrEmploymentRelatedPage, WhatTypeOfBeneficiaryPage}
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, CompanyBeneficiaries, IndividualBeneficiaries}

class BeneficiaryNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String, userAnswers: ReadableUserAnswers): Call =
    nextPage(page, draftId, userAnswers)

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    route(draftId, config)(page)(userAnswers)

  private def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddABeneficiaryPage => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => whatTypeOfBeneficiaryRoute(draftId)
    case CharityOrTrustPage => charityOrTrust(draftId, 0)
    case ClassBeneficiaryDescriptionPage(_) => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case CompanyOrEmploymentRelatedPage => companyOrEmploymentRelatedPage(draftId)
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
  }

  private def routeToCompanyBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val companyBeneficiaries = userAnswers.get(CompanyBeneficiaries).getOrElse(List.empty)
    companyBeneficiaries match {
      case Nil =>
        companyRoutes.NameController.onPageLoad(0, draftId)
      case t if t.nonEmpty =>
        companyRoutes.NameController.onPageLoad(t.size, draftId)
    }
  }

  private def companyOrEmploymentRelatedPage(draftId: String)(userAnswers: ReadableUserAnswers): Call =
    userAnswers.get(CompanyOrEmploymentRelatedPage) match {
      case Some(Company) => routeToCompanyBeneficiaryIndex(userAnswers, draftId)
      case _ => controllers.routes.FeatureNotAvailableController.onPageLoad()
    }


  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def charityOrTrust(draftId: String, index: Int)(userAnswers: ReadableUserAnswers): Call = userAnswers.get(CharityOrTrustPage) match {
    case Some(Charity) => charityRoutes.CharityNameController.onPageLoad(index, draftId)
    case Some(Trust) => trustRoutes.NameController.onPageLoad(index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: ReadableUserAnswers): Call = {
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

  private def routeToCharityOrTrustIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val charityortrust = userAnswers.get(CharityOrTrustPage).getOrElse(List.empty)
    charityortrust match {
      case Nil =>
        charityortrustRoutes.CharityOrTrustController.onPageLoad(NormalMode, draftId)
      case _ =>
        charityortrustRoutes.CharityOrTrustController.onPageLoad(NormalMode, draftId)
    }
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    indBeneficiaries match {
      case list =>
        individualRts.NameController.onPageLoad(list.size, draftId)
    }
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String) = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiaries match {
      case list =>
        classOfBeneficiariesRts.ClassBeneficiaryDescriptionController.onPageLoad(list.size, draftId)
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


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

import config.FrontendAppConfig
import controllers.register.beneficiaries.charityortrust.charity.{routes => charityRoutes}
import controllers.register.beneficiaries.charityortrust.trust.{routes => trustRoutes}
import controllers.register.beneficiaries.charityortrust.{routes => charityortrustRoutes}
import controllers.register.beneficiaries.classofbeneficiaries.{routes => classOfBeneficiariesRts}
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => companyRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => largeRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.{routes => companyOrEmploymentRelatedRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRts}
import controllers.register.beneficiaries.other.{routes => otherRoutes}
import javax.inject.Inject
import models.CompanyOrEmploymentRelatedToAdd.{Company, _}
import models.ReadableUserAnswers
import models.registration.pages.CharityOrTrust.{Charity, Trust, _}
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import pages.Page
import pages.register.beneficiaries._
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, CompanyBeneficiaries, IndividualBeneficiaries, _}

class BeneficiaryNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    nextPage(page, draftId, false, userAnswers)

  override def nextPage(page: Page, draftId: String, fiveMldEnabled: Boolean, userAnswers: ReadableUserAnswers): Call =
    route(draftId, config)(page)(userAnswers)

  private def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddABeneficiaryPage => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => whatTypeOfBeneficiaryRoute(draftId)
    case CharityOrTrustPage => charityOrTrustRoute(draftId)
    case CompanyOrEmploymentRelatedPage => companyOrEmploymentRelatedRoute(draftId)
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
  }

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: ReadableUserAnswers): Call = {
    val whatBeneficiaryToAdd = userAnswers.get(WhatTypeOfBeneficiaryPage)
    whatBeneficiaryToAdd match {
      case Some(WhatTypeOfBeneficiary.Individual) =>
        routeToIndividualBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.ClassOfBeneficiary) =>
        routeToClassOfBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.CharityOrTrust) =>
        charityortrustRoutes.CharityOrTrustController.onPageLoad(draftId)
      case Some(WhatTypeOfBeneficiary.Charity) =>
        routeToCharityBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.Trust) =>
        routeToTrustBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.CompanyOrEmployment) =>
        companyOrEmploymentRelatedRoutes.CompanyOrEmploymentRelatedController.onPageLoad(draftId)
      case Some(WhatTypeOfBeneficiary.Company) =>
        routeToCompanyBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.Employment) =>
        routeToEmploymentBeneficiaryIndex(userAnswers, draftId)
      case Some(WhatTypeOfBeneficiary.Other) =>
        routeToOtherBeneficiaryIndex(userAnswers, draftId)
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def charityOrTrustRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(CharityOrTrustPage) match {
      case Some(Charity) => routeToCharityBeneficiaryIndex(userAnswers, draftId)
      case Some(Trust) => routeToTrustBeneficiaryIndex(userAnswers, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToCharityBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val charityBeneficiaries = userAnswers.get(CharityBeneficiaries).getOrElse(List.empty)
    charityRoutes.CharityNameController.onPageLoad(charityBeneficiaries.size, draftId)
  }

  private def routeToTrustBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val trustBeneficiaries = userAnswers.get(TrustBeneficiaries).getOrElse(List.empty)
    trustRoutes.NameController.onPageLoad(trustBeneficiaries.size, draftId)
  }

  private def companyOrEmploymentRelatedRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(CompanyOrEmploymentRelatedPage) match {
      case Some(Company) => routeToCompanyBeneficiaryIndex(userAnswers, draftId)
      case Some(EmploymentRelated) => routeToEmploymentBeneficiaryIndex(userAnswers, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToCompanyBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val companyBeneficiaries = userAnswers.get(CompanyBeneficiaries).getOrElse(List.empty)
    companyRoutes.NameController.onPageLoad(companyBeneficiaries.size, draftId)
  }

  private def routeToEmploymentBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val employmentRelatedBeneficiaries = userAnswers.get(LargeBeneficiaries).getOrElse(List.empty)
    largeRoutes.NameController.onPageLoad(employmentRelatedBeneficiaries.size, draftId)
  }

  private def routeToIndividualBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val indBeneficiaries = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    individualRts.NameController.onPageLoad(indBeneficiaries.size, draftId)
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
    classOfBeneficiariesRts.ClassBeneficiaryDescriptionController.onPageLoad(classOfBeneficiaries.size, draftId)
  }

  private def routeToOtherBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val beneficiaries = userAnswers.get(OtherBeneficiaries).getOrElse(List.empty)
    otherRoutes.DescriptionController.onPageLoad(beneficiaries.size, draftId)
  }

  private def addABeneficiaryYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val add = answers.get(AddABeneficiaryYesNoPage)

    add match {
      case Some(true) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(false) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addABeneficiaryRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
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


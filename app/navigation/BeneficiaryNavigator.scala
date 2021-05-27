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
import models.CompanyOrEmploymentRelatedToAdd.{Company, _}
import models.ReadableUserAnswers
import models.registration.pages.CharityOrTrust.{Charity, Trust, _}
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import pages.register.beneficiaries._
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import pages.{Page, QuestionPage}
import play.api.libs.json.Reads
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, CompanyBeneficiaries, IndividualBeneficiaries, _}
import uk.gov.hmrc.http.HttpVerbs.GET
import viewmodels.addAnother.ViewModel

import javax.inject.Inject

class BeneficiaryNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
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
    Call(GET, config.registrationProgressUrl(draftId))
  }

  private def whatTypeOfBeneficiaryRoute(draftId: String)(userAnswers: ReadableUserAnswers): Call = {
    userAnswers.get(WhatTypeOfBeneficiaryPage) match {
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

  private def routeToIndividualBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, IndividualBeneficiaries, individualRts.NameController.onPageLoad, draftId)
  }

  private def routeToClassOfBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, ClassOfBeneficiaries, classOfBeneficiariesRts.ClassBeneficiaryDescriptionController.onPageLoad, draftId)
  }

  private def charityOrTrustRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(CharityOrTrustPage) match {
      case Some(Charity) => routeToCharityBeneficiaryIndex(userAnswers, draftId)
      case Some(Trust) => routeToTrustBeneficiaryIndex(userAnswers, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToCharityBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, CharityBeneficiaries, charityRoutes.CharityNameController.onPageLoad, draftId)
  }

  private def routeToTrustBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, TrustBeneficiaries, trustRoutes.NameController.onPageLoad, draftId)
  }

  private def companyOrEmploymentRelatedRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(CompanyOrEmploymentRelatedPage) match {
      case Some(Company) => routeToCompanyBeneficiaryIndex(userAnswers, draftId)
      case Some(EmploymentRelated) => routeToEmploymentBeneficiaryIndex(userAnswers, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToCompanyBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, CompanyBeneficiaries, companyRoutes.NameController.onPageLoad, draftId)
  }

  private def routeToEmploymentBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, LargeBeneficiaries, largeRoutes.NameController.onPageLoad, draftId)
  }

  private def routeToOtherBeneficiaryIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    routeToBeneficiaryIndex(userAnswers, OtherBeneficiaries, otherRoutes.DescriptionController.onPageLoad, draftId)
  }

  private def routeToBeneficiaryIndex[T <: ViewModel](userAnswers: ReadableUserAnswers,
                                                      page: QuestionPage[List[T]],
                                                      route: (Int, String) => Call,
                                                      draftId: String)
                                                     (implicit rds: Reads[T]): Call = {
    val beneficiaries = userAnswers.get(page).getOrElse(List.empty)
    val index = beneficiaries match {
      case Nil => 0
      case x if !x.last.isComplete => x.size - 1
      case x => x.size
    }
    route(index, draftId)
  }

  private def addABeneficiaryYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    answers.get(AddABeneficiaryYesNoPage) match {
      case Some(true) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(false) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addABeneficiaryRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    answers.get(AddABeneficiaryPage) match {
      case Some(AddABeneficiary.YesNow) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(AddABeneficiary.YesLater) =>
        assetsCompletedRoute(draftId, config)
      case Some(AddABeneficiary.NoComplete) =>
        assetsCompletedRoute(draftId, config)
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

}

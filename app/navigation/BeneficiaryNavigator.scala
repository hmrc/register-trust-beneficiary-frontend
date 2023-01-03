/*
 * Copyright 2023 HM Revenue & Customs
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
import models.registration.pages.CharityOrTrust.{Charity, Trust, _}
import models.registration.pages.WhatTypeOfBeneficiary._
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{Beneficiaries, ReadableUserAnswers}
import pages.Page
import pages.register.beneficiaries._
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.Constants.MAX
import viewmodels.addAnother._

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
      case Some(typeOfBeneficiary) => BeneficiaryNavigator.addBeneficiaryNowRoute(typeOfBeneficiary, userAnswers.beneficiaries, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad
    }
  }

  private def charityOrTrustRoute(draftId: String)(userAnswers: ReadableUserAnswers): Call =
    userAnswers.get(CharityOrTrustPage) match {
      case Some(Charity) => BeneficiaryNavigator.routeToCharityBeneficiaryIndex(userAnswers.beneficiaries.charities, draftId)
      case Some(Trust) => BeneficiaryNavigator.routeToTrustBeneficiaryIndex(userAnswers.beneficiaries.trusts, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad
    }

  private def companyOrEmploymentRelatedRoute(draftId: String)(userAnswers: ReadableUserAnswers): Call =
    userAnswers.get(CompanyOrEmploymentRelatedPage) match {
      case Some(Company) => BeneficiaryNavigator.routeToCompanyBeneficiaryIndex(userAnswers.beneficiaries.companies, draftId)
      case Some(EmploymentRelated) => BeneficiaryNavigator.routeToEmploymentBeneficiaryIndex(userAnswers.beneficiaries.large, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad
    }

  private def addABeneficiaryYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    answers.get(AddABeneficiaryYesNoPage) match {
      case Some(true) =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
      case Some(false) => assetsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad
    }
  }

  private def addABeneficiaryRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    answers.get(AddABeneficiaryPage) match {
      case Some(AddABeneficiary.YesNow) =>
        BeneficiaryNavigator.addBeneficiaryRoute(answers.beneficiaries, draftId)
      case Some(_) =>
        assetsCompletedRoute(draftId, config)
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad
    }
  }

}

object BeneficiaryNavigator {

  def addBeneficiaryRoute(beneficiaries: Beneficiaries, draftId: String): Call = {
    val routes: List[(List[ViewModel], Call)] = List(
      (beneficiaries.individuals, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.Individual, beneficiaries, draftId)),
      (beneficiaries.unidentified, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.ClassOfBeneficiary, beneficiaries, draftId)),
      (beneficiaries.companies, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.Company, beneficiaries, draftId)),
      (beneficiaries.large, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.Employment, beneficiaries, draftId)),
      (beneficiaries.trusts, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.Trust, beneficiaries, draftId)),
      (beneficiaries.charities, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.Charity, beneficiaries, draftId)),
      (beneficiaries.other, addBeneficiaryNowRoute(WhatTypeOfBeneficiary.Other, beneficiaries, draftId))
    )

    routes.filter(_._1.size < MAX) match {
      case (_, x) :: Nil =>
        x
      case (x, _) :: (y, _) :: Nil if x == beneficiaries.companies && y == beneficiaries.large =>
        addBeneficiaryNowRoute(WhatTypeOfBeneficiary.CompanyOrEmployment, beneficiaries, draftId)
      case (x, _) :: (y, _) :: Nil if x == beneficiaries.trusts && y == beneficiaries.charities =>
        addBeneficiaryNowRoute(CharityOrTrust, beneficiaries, draftId)
      case _ =>
        controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId)
    }
  }

  def addBeneficiaryNowRoute(`type`: WhatTypeOfBeneficiary, beneficiaries: Beneficiaries, draftId: String): Call = {
    `type` match {
      case WhatTypeOfBeneficiary.Individual => routeToIndividualBeneficiaryIndex(beneficiaries.individuals, draftId)
      case WhatTypeOfBeneficiary.ClassOfBeneficiary => routeToClassOfBeneficiaryIndex(beneficiaries.unidentified, draftId)
      case WhatTypeOfBeneficiary.CompanyOrEmployment => companyOrEmploymentRelatedRoutes.CompanyOrEmploymentRelatedController.onPageLoad(draftId)
      case WhatTypeOfBeneficiary.Company => routeToCompanyBeneficiaryIndex(beneficiaries.companies, draftId)
      case WhatTypeOfBeneficiary.Employment => routeToEmploymentBeneficiaryIndex(beneficiaries.large, draftId)
      case WhatTypeOfBeneficiary.CharityOrTrust => charityortrustRoutes.CharityOrTrustController.onPageLoad(draftId)
      case WhatTypeOfBeneficiary.Trust => routeToTrustBeneficiaryIndex(beneficiaries.trusts, draftId)
      case WhatTypeOfBeneficiary.Charity => routeToCharityBeneficiaryIndex(beneficiaries.charities, draftId)
      case WhatTypeOfBeneficiary.Other => routeToOtherBeneficiaryIndex(beneficiaries.other, draftId)
    }
  }

  private def routeToIndividualBeneficiaryIndex(beneficiaries: List[IndividualBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, individualRts.NameController.onPageLoad, draftId)
  }

  private def routeToClassOfBeneficiaryIndex(beneficiaries: List[ClassOfBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, classOfBeneficiariesRts.ClassBeneficiaryDescriptionController.onPageLoad, draftId)
  }

  private def routeToCharityBeneficiaryIndex(beneficiaries: List[CharityBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, charityRoutes.CharityNameController.onPageLoad, draftId)
  }

  private def routeToTrustBeneficiaryIndex(beneficiaries: List[TrustBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, trustRoutes.NameController.onPageLoad, draftId)
  }

  private def routeToCompanyBeneficiaryIndex(beneficiaries: List[CompanyBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, companyRoutes.NameController.onPageLoad, draftId)
  }

  private def routeToEmploymentBeneficiaryIndex(beneficiaries: List[EmploymentRelatedBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, largeRoutes.NameController.onPageLoad, draftId)
  }

  private def routeToOtherBeneficiaryIndex(beneficiaries: List[OtherBeneficiaryViewModel], draftId: String): Call = {
    routeToBeneficiaryIndex(beneficiaries, otherRoutes.DescriptionController.onPageLoad, draftId)
  }

  private def routeToBeneficiaryIndex[T <: ViewModel](beneficiaries: List[T],
                                                      route: (Int, String) => Call,
                                                      draftId: String): Call = {
    route(beneficiaries.size, draftId)
  }
}

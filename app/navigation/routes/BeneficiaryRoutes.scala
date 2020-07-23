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
import controllers.register.beneficiaries.charityortrust.trust.{routes => trustRoutes}
import models.registration.pages.CharityOrTrust.{Charity, Trust}
import models.{NormalMode, ReadableUserAnswers}
import pages.Page
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, charity}
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._
import play.api.mvc.Call

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
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

  private def charityortrust(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(CharityOrTrustPage) match {
    case Some(Charity) => charityRoutes.CharityNameController.onPageLoad(NormalMode, index, draftId)
    case Some(Trust) => trustRoutes.NameController.onPageLoad(index, draftId)
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


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
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual._
import play.api.mvc.Call

object BeneficiaryRoutes {
  def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case ClassBeneficiaryDescriptionPage(_) => _ => controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)
    case CharityOrTrustPage => charityOrTrust(draftId, 0)
  }

  private def charityOrTrust(draftId: String, index: Int)(userAnswers: ReadableUserAnswers) : Call = userAnswers.get(CharityOrTrustPage) match {
    case Some(Charity) => charityRoutes.CharityNameController.onPageLoad(index, draftId)
    case Some(Trust) => trustRoutes.NameController.onPageLoad(index, draftId)
    case _ => controllers.routes.SessionExpiredController.onPageLoad()
  }
}


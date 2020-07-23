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
import javax.inject.Inject
import models.registration.pages.{AddABeneficiary, WhatTypeOfBeneficiary}
import models.{Mode, ReadableUserAnswers}
import pages.Page
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage, WhatTypeOfBeneficiaryPage}
import play.api.mvc.Call
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}

class BeneficiaryNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String, userAnswers: ReadableUserAnswers): Call =
    nextPage(page, draftId, userAnswers)

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    route(draftId, config)(page)(userAnswers)

  private def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddABeneficiaryPage => addABeneficiaryRoute(draftId, config)
    case AddABeneficiaryYesNoPage => addABeneficiaryYesNoRoute(draftId, config)
    case WhatTypeOfBeneficiaryPage => whatTypeOfBeneficiaryRoute(draftId)
  }

  private def assetsCompletedRoute(draftId: String, config: FrontendAppConfig) : Call = {
    Call("GET", config.registrationProgressUrl(draftId))
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


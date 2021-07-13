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

package controllers.register.beneficiaries

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries._
import javax.inject.Inject

class InfoController @Inject()(
                                override val messagesApi: MessagesApi,
                                identify: RegistrationIdentifierAction,
                                getData: DraftIdRetrievalActionProvider,
                                requireData: RegistrationDataRequiredAction,
                                val controllerComponents: MessagesControllerComponents,
                                view4MLD: Info4mldView,
                                view5MLD: Info5mldView
                              ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>
      val ua = request.userAnswers
      if (ua.is5mldEnabled) {
        Ok(view5MLD(draftId, ua.isTaxable))
      } else {
        Ok(view4MLD(draftId))
      }
  }

  def onSubmit(draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    _ =>
      Redirect(routes.WhatTypeOfBeneficiaryController.onPageLoad(draftId))
  }
}

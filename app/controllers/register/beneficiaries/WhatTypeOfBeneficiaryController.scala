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
import forms.WhatTypeOfBeneficiaryFormProvider
import javax.inject.Inject
import models.Enumerable
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries.WhatTypeOfBeneficiaryView

import scala.concurrent.{ExecutionContext, Future}

class WhatTypeOfBeneficiaryController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 registrationsRepository: RegistrationsRepository,
                                                 navigator: Navigator,
                                                 identify: RegistrationIdentifierAction,
                                                 getData: DraftIdRetrievalActionProvider,
                                                 requireData: RegistrationDataRequiredAction,
                                                 formProvider: WhatTypeOfBeneficiaryFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: WhatTypeOfBeneficiaryView
                                               )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with Enumerable.Implicits
    with AnyBeneficiaries {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] = identify andThen getData(draftId) andThen requireData

  private val form = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>
      Ok(view(
        form,
        draftId,
        isAnyBeneficiaryAdded(request.userAnswers),
        beneficiaries(request.userAnswers).nonMaxedOutOptions)
      )
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(
            formWithErrors,
            draftId,
            isAnyBeneficiaryAdded(request.userAnswers),
            beneficiaries(request.userAnswers).nonMaxedOutOptions
          ))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(WhatTypeOfBeneficiaryPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatTypeOfBeneficiaryPage, draftId, updatedAnswers))
        }
      )
  }

}

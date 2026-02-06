/*
 * Copyright 2026 HM Revenue & Customs
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

import cats.data.EitherT
import controllers.actions.register.{
  DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction
}
import forms.WhatTypeOfBeneficiaryFormProvider
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.WhatTypeOfBeneficiaryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhatTypeOfBeneficiaryController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  navigator: Navigator,
  identify: RegistrationIdentifierAction,
  getData: DraftIdRetrievalActionProvider,
  requireData: RegistrationDataRequiredAction,
  formProvider: WhatTypeOfBeneficiaryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhatTypeOfBeneficiaryView,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getSimpleName

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen getData(draftId) andThen requireData

  private val form = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(WhatTypeOfBeneficiaryPage) match {
      case Some(value) => form.fill(value)
      case None        => form
    }

    Ok(
      view(
        form = preparedForm,
        draftId = draftId,
        beneficiaryAdded = request.userAnswers.isAnyBeneficiaryAdded,
        options = request.userAnswers.beneficiaries.nonMaxedOutOptions
      )
    )
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(
            BadRequest(
              view(
                form = formWithErrors,
                draftId = draftId,
                beneficiaryAdded = request.userAnswers.isAnyBeneficiaryAdded,
                options = request.userAnswers.beneficiaries.nonMaxedOutOptions
              )
            )
          ),
        value => {
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(WhatTypeOfBeneficiaryPage, value)))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(WhatTypeOfBeneficiaryPage, draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_)     =>
              logger.warn(s"[$className][onSubmit][Session ID: ${request.sessionId}] Error while storing user answers")
              InternalServerError(technicalErrorView())
          }
        }
      )
  }

}

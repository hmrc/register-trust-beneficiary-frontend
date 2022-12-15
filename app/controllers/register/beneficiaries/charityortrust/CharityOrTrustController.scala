/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.register.beneficiaries.charityortrust

import cats.data.EitherT
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.CharityOrTrustFormProvider
import models.registration.pages.CharityOrTrust
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.CharityOrTrustView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharityOrTrustController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          registrationsRepository: RegistrationsRepository,
                                          navigator: Navigator,
                                          identify: RegistrationIdentifierAction,
                                          getData: DraftIdRetrievalActionProvider,
                                          requireData: RegistrationDataRequiredAction,
                                          formProvider: CharityOrTrustFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: CharityOrTrustView,
                                          technicalErrorView: TechnicalErrorView
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getName

  private val form: Form[CharityOrTrust] = formProvider()

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen
      getData(draftId) andThen
      requireData

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CharityOrTrustPage) match {
        case Some(value) => form.fill(value)
        case None => form
      }

      Ok(view(preparedForm, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId))),
        value => {
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CharityOrTrustPage, value)))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CharityOrTrustPage, draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_) =>
              logger.warn(s"[$className][onSubmit][Session ID: ${request.sessionId}] Error while storing user answers.")
              InternalServerError(technicalErrorView())
          }
        }
      )
  }
}

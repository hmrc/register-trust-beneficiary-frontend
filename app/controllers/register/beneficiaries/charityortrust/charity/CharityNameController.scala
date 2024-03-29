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

package controllers.register.beneficiaries.charityortrust.charity

import cats.data.EitherT
import cats.implicits._
import config.annotations.CharityBeneficiary
import controllers.actions.StandardActionSets
import forms.StringFormProvider
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.charity.CharityNamePage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.charity.CharityNameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharityNameController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       repository: RegistrationsRepository,
                                       @CharityBeneficiary navigator: Navigator,
                                       standardActionSets: StandardActionSets,
                                       formProvider: StringFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: CharityNameView,
                                       technicalErrorView: TechnicalErrorView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getSimpleName
  private val maximumLength = 105

  private val form: Form[String] = formProvider.withPrefix("charity.name", maximumLength)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CharityNamePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index))),

        value => {
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CharityNamePage(index), value)))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CharityNamePage(index), draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_) =>
              logger.warn(s"[$className][onSubmit][Session ID: ${request.sessionId}] Error while storing user answers")
              InternalServerError(technicalErrorView())
          }
        }
      )
  }
}

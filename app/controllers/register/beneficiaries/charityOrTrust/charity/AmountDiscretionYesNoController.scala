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

package controllers.register.beneficiaries.charityortrust.charity

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.actions.{RequiredAnswer, RequiredAnswerActionProvider}
import forms.YesNoFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.charity.{AmountDiscretionYesNoPage, CharityNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.beneficiaries.charityortrust.charity.AmountDiscretionYesNoView

import scala.concurrent.{ExecutionContext, Future}

class AmountDiscretionYesNoController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 registrationsRepository: RegistrationsRepository,
                                                 navigator: Navigator,
                                                 identify: RegistrationIdentifierAction,
                                                 getData: DraftIdRetrievalActionProvider,
                                                 requireData: RegistrationDataRequiredAction,
                                                 requiredAnswer: RequiredAnswerActionProvider,
                                                 formProvider: YesNoFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: AmountDiscretionYesNoView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("charity.discretionYesNo")

  private def actions(draftId: String, index: Int) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(CharityNamePage(index), routes.CharityNameController.onPageLoad(NormalMode, index, draftId)))

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(draftId, index) {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get

      val preparedForm = request.userAnswers.get(AmountDiscretionYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, charityName))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(draftId, index).async {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, charityName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AmountDiscretionYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AmountDiscretionYesNoPage(index), draftId, updatedAnswers))
      )
  }
}

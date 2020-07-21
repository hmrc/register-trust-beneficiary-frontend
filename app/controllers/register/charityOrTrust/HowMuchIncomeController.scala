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

package controllers.register.charityOrTrust

import controllers.actions.{RequiredAnswer, RequiredAnswerActionProvider}
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.HowMuchIncomeFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.beneficiaries.charityOrTrust.{CharityNamePage, HowMuchIncomePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.beneficiaries.charityOrTrust.HowMuchIncomeView

import scala.concurrent.{ExecutionContext, Future}

class HowMuchIncomeController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      registrationsRepository: RegistrationsRepository,
                                                      navigator: Navigator,
                                                      identify: RegistrationIdentifierAction,
                                                      getData: DraftIdRetrievalActionProvider,
                                                      requireData: RegistrationDataRequiredAction,
                                                      requiredAnswer: RequiredAnswerActionProvider,
                                                      formProvider: HowMuchIncomeFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: HowMuchIncomeView)
                                       (implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(draftId: String, index: Int) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(CharityNamePage(index), routes.CharityNameController.onPageLoad(NormalMode, index, draftId)))

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(draftId, index) {
    implicit request =>

      val preparedForm = request.userAnswers.get(HowMuchIncomePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(draftId, index).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(HowMuchIncomePage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(HowMuchIncomePage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}

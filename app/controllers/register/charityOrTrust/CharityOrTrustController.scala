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

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.{CharityOrTrustFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.register.beneficiaries.charityOrTrust.CharityOrTrustPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.beneficiaries.charityOrTrust.CharityOrTrustView

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
                                          view: CharityOrTrustView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  private def actions(draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CharityOrTrustPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CharityOrTrustPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CharityOrTrustPage, mode, draftId)(updatedAnswers))
      )
  }
}
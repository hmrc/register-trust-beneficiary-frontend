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

import controllers.actions._
import controllers.actions.register._
import forms.UKAddressFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.register.beneficiaries.charityOrTrust.{CharityAddressUKPage, CharityNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.beneficiaries.charityOrTrust.CharityAddressUKView

import scala.concurrent.{ExecutionContext, Future}

class CharityAddressUKController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     registrationsRepository: RegistrationsRepository,
                                     navigator: Navigator,
                                     identify: RegistrationIdentifierAction,
                                     getData: DraftIdRetrievalActionProvider,
                                     requireData: RegistrationDataRequiredAction,
                                     requiredAnswer: RequiredAnswerActionProvider,
                                     formProvider: UKAddressFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: CharityAddressUKView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(CharityNamePage(index), routes.CharityNameController.onPageLoad(NormalMode, index, draftId)))

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get.toString

      val preparedForm = request.userAnswers.get(CharityAddressUKPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, charityName, index))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get.toString

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, charityName, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CharityAddressUKPage(index), value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CharityAddressUKPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}

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

package controllers.register.beneficiaries.individualBeneficiary

import cats.data.EitherT
import config.annotations.IndividualBeneficiary
import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import errors.TrustErrors
import forms.NationalInsuranceNumberFormProvider
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.beneficiaries.individual.{NamePage, NationalInsuranceNumberPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.individualBeneficiary.NationalInsuranceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NationalInsuranceNumberController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   registrationsRepository: RegistrationsRepository,
                                                   @IndividualBeneficiary navigator: Navigator,
                                                   identify: RegistrationIdentifierAction,
                                                   getData: DraftIdRetrievalActionProvider,
                                                   requireData: RegistrationDataRequiredAction,
                                                   requiredAnswer: RequiredAnswerActionProvider,
                                                   formProvider: NationalInsuranceNumberFormProvider,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   view: NationalInsuranceNumberView,
                                                   technicalErrorView: TechnicalErrorView
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form(index: Int)(implicit request: RegistrationDataRequest[AnyContent]) =
    formProvider.withPrefix("individualBeneficiaryNationalInsuranceNumber", request.userAnswers, index)

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(NamePage(index), routes.NameController.onPageLoad(index, draftId)))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val name = request.userAnswers.get(NamePage(index)).get

      val preparedForm = request.userAnswers.get(NationalInsuranceNumberPage(index)) match {
        case None => form(index)
        case Some(value) => form(index).fill(value)
      }

      Ok(view(preparedForm, draftId, name, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val name = request.userAnswers.get(NamePage(index)).get

      form(index).bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, name, index))),

        value => {
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(NationalInsuranceNumberPage(index), value)))
            _ <- EitherT.right[TrustErrors](registrationsRepository.set(updatedAnswers))
          } yield Redirect(navigator.nextPage(NationalInsuranceNumberPage(index), draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_) => InternalServerError(technicalErrorView())
          }
        }
      )
  }
}

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

package controllers.register.beneficiaries.individualBeneficiary

import cats.data.EitherT
import config.annotations.IndividualBeneficiary
import controllers.actions.register.{
  DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction
}
import controllers.actions.{RequiredAnswer, RequiredAnswerActionProvider}
import controllers.filters.IndexActionFilterProvider
import forms.InternationalAddressFormProvider
import navigation.Navigator
import pages.register.beneficiaries.individual.{AddressInternationalPage, NamePage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.beneficiaries.IndividualBeneficiaries
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.individualBeneficiary.AddressInternationalView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressInternationalController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @IndividualBeneficiary navigator: Navigator,
  identify: RegistrationIdentifierAction,
  getData: DraftIdRetrievalActionProvider,
  validateIndex: IndexActionFilterProvider,
  requireData: RegistrationDataRequiredAction,
  requiredAnswer: RequiredAnswerActionProvider,
  formProvider: InternationalAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddressInternationalView,
  val countryOptions: CountryOptionsNonUK,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getSimpleName

  private val form = formProvider()

  private def actions(index: Int, draftId: String) =
    identify                                        andThen
      getData(draftId)                              andThen
      requireData                                   andThen
      validateIndex(index, IndividualBeneficiaries) andThen
      requiredAnswer(RequiredAnswer(NamePage(index), routes.NameController.onPageLoad(index, draftId)))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) { implicit request =>
    val name = request.userAnswers.get(NamePage(index)).get

    val preparedForm = request.userAnswers.get(AddressInternationalPage(index)) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, countryOptions.options(), index, draftId, name.toString))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async { implicit request =>
    val name = request.userAnswers.get(NamePage(index)).get

    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options(), index, draftId, name.toString))),
        value => {
          val result = for {
            updatedAnswers <-
              EitherT(Future.successful(request.userAnswers.set(AddressInternationalPage(index), value)))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddressInternationalPage(index), draftId, updatedAnswers))

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

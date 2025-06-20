/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.register.beneficiaries.companyoremploymentrelated.company.mld5

import cats.data.EitherT
import config.annotations.CompanyBeneficiary
import controllers.actions._
import controllers.actions.register.company.NameRequiredAction
import forms.YesNoFormProvider
import handlers.ErrorHandler
import navigation.Navigator
import pages.register.beneficiaries.companyoremploymentrelated.company.NamePage
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5.CountryOfResidenceYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.companyoremploymentrelated.company.mld5.CountryOfResidenceYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceYesNoController @Inject()(
                                                   val controllerComponents: MessagesControllerComponents,
                                                   repository: RegistrationsRepository,
                                                   @CompanyBeneficiary navigator: Navigator,
                                                   standardActionSets: StandardActionSets,
                                                   nameAction: NameRequiredAction,
                                                   formProvider: YesNoFormProvider,
                                                   view: CountryOfResidenceYesNoView,
                                                   technicalErrorView: TechnicalErrorView,
                                                   errorHandler: ErrorHandler
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getSimpleName
  private val form: Form[Boolean] = formProvider.withPrefix("companyBeneficiary.5mld.countryOfResidenceYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
      implicit request =>
        handlePageLoad(index, draftId)
    }

  def handlePageLoad(index: Int, draftId: String)(implicit request: BeneficiaryNameRequest[_]): Future[Result] = {
    request.userAnswers.get(NamePage(index)) match {
      case Some(trustName) =>
        val preparedForm = request.userAnswers.get(CountryOfResidenceYesNoPage(index)) match {
          case None =>
            form
          case Some(value) =>
            form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, draftId, index, trustName)))
      case None =>
        logger.warn(s"[$className][handlePageLoad][Session ID: ${request.request.sessionId}] Error while getting trust name")
        errorHandler.notFoundTemplate.map(NotFound(_))
    }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
      implicit request =>

        val trustName = request.userAnswers.get(NamePage(index)).get

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, trustName))),

          value => {
            val result = for {
              updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CountryOfResidenceYesNoPage(index), value)))
              _ <- repository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, updatedAnswers))

            result.value.map {
              case Right(call) => call
              case Left(_) =>
                logger.warn(s"[$className][onSubmit][Session ID: ${request.request.sessionId}] Error while storing user answers")
                InternalServerError(technicalErrorView())
            }
          }
        )
    }
}

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

package controllers.register.beneficiaries.companyoremploymentrelated.company

import cats.data.EitherT
import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.register.company.NameRequiredAction
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.CompanyBeneficiaryStatus
import pages.register.beneficiaries.AnswersPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.CompanyBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.companyoremploymentrelated.company.CheckDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        registrationsRepository: RegistrationsRepository,
                                        navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: CompanyBeneficiaryPrintHelper,
                                        nameAction: NameRequiredAction,
                                        technicalErrorView: TechnicalErrorView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getSimpleName

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val section: AnswerSection = printHelper.checkDetailsSection(request.userAnswers, request.beneficiaryName, index, draftId)
      Ok(view(Seq(section), index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(CompanyBeneficiaryStatus(index), Completed)

      val result = for {
        updatedAnswers <- EitherT(Future.successful(answers))
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(AnswersPage, draftId, request.userAnswers))

      result.value.map {
        case Right(call) => call
        case Left(_) =>
          logger.warn(s"[$className][onSubmit][Session ID: ${request.sessionId}] Error while storing user answers")
          InternalServerError(technicalErrorView())
      }
  }
}

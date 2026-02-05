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

package controllers

import connectors.SubmissionDraftConnector
import controllers.actions.register.RegistrationIdentifierAction
import controllers.register.beneficiaries.routes._
import models.{TaskStatus, UserAnswers}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  repository: RegistrationsRepository,
  identify: RegistrationIdentifierAction,
  submissionDraftConnector: SubmissionDraftConnector,
  trustStoreService: TrustsStoreService,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val className = getClass.getSimpleName

  def onPageLoad(draftId: String): Action[AnyContent] = identify.async { implicit request =>
    val result = for {
      isTaxable      <- submissionDraftConnector.getIsTrustTaxable(draftId)
      optUserAnswers <- repository.get(draftId)
      userAnswers     = extractUserAnswers(optUserAnswers, draftId, request.identifier, isTaxable)
      _              <- repository.set(userAnswers)
      _              <- trustStoreService.updateTaskStatus(draftId, TaskStatus.InProgress)
    } yield
      if (userAnswers.isAnyBeneficiaryAdded) {
        Redirect(AddABeneficiaryController.onPageLoad(draftId))
      } else {
        Redirect(InfoController.onPageLoad(draftId))
      }

    result.value.map {
      case Right(call) => call
      case Left(_)     =>
        logger.warn(s"[$className][onSubmit] Error while storing user answers")
        InternalServerError(technicalErrorView())
    }
  }

  private def extractUserAnswers(
    optUserAnswers: Option[UserAnswers],
    draftId: String,
    identifier: String,
    isTaxable: Boolean
  ): UserAnswers =
    optUserAnswers match {
      case Some(userAnswers) => userAnswers.copy(isTaxable = isTaxable)
      case _                 => UserAnswers(draftId, Json.obj(), identifier, isTaxable)
    }

}

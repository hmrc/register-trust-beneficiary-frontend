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

package controllers.register.beneficiaries.charityortrust.charity

import cats.data.EitherT
import controllers.actions._
import controllers.actions.register.charity.NameRequiredAction
import errors.TrustErrors
import models.Status.Completed
import pages.entitystatus.CharityBeneficiaryStatus
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.CharityBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.charity.CharityAnswersView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharityAnswersController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          repository: RegistrationsRepository,
                                          standardActionSets: StandardActionSets,
                                          nameAction: NameRequiredAction,
                                          view: CharityAnswersView,
                                          printHelper: CharityBeneficiaryPrintHelper,
                                          technicalErrorView: TechnicalErrorView
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val section: AnswerSection = printHelper.checkDetailsSection(request.userAnswers, request.beneficiaryName, index, draftId)
      Ok(view(Seq(section), index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
    implicit request =>

      val result = for {
        updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CharityBeneficiaryStatus(index), Completed)))
        _ <- EitherT.right[TrustErrors](repository.set(updatedAnswers))
      } yield Redirect(controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId))

      result.value.map {
        case Right(call) => call
        case Left(_) => InternalServerError(technicalErrorView())
      }
  }
}

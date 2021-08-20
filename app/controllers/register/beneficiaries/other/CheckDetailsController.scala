/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.beneficiaries.other

import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.register.other.DescriptionRequiredAction
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.OtherBeneficiaryStatus
import pages.register.beneficiaries.{AnswersPage, WhatTypeOfBeneficiaryPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.OtherBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.register.beneficiaries.other.CheckDetailsView

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
                                        descriptionRequiredAction: DescriptionRequiredAction,
                                        printHelper: OtherBeneficiaryPrintHelper
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(descriptionRequiredAction(index)) {
      implicit request =>

        val section: AnswerSection = printHelper.checkDetailsSection(request.userAnswers, request.description, index, draftId)
        Ok(view(Seq(section), index, draftId))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).async {
      implicit request =>

        val answers = request.userAnswers.set(OtherBeneficiaryStatus(index), Completed)

        for {
          updatedAnswers <- Future.fromTry(answers)
          _ <- registrationsRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(AnswersPage, draftId, request.userAnswers))
    }
}

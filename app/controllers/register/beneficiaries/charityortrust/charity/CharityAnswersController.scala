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

package controllers.register.beneficiaries.charityortrust.charity

import controllers.actions._
import controllers.actions.register.company.NameRequiredAction
import javax.inject.Inject
import models.Status.Completed
import pages.entitystatus.CharityBeneficiaryStatus
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.answers.CharityBeneficiaryAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.beneficiaries.charityortrust.charity.CharityAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CharityAnswersController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          repository: RegistrationsRepository,
                                          standardActionSets: StandardActionSets,
                                          nameAction: NameRequiredAction,
                                          view: CharityAnswersView,
                                          countryOptions: CountryOptions
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val answers = new CharityBeneficiaryAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          answers.charityBeneficiaryRows(index)
        )
      )

      Ok(view(index, draftId, sections))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
    implicit request =>

      val answers = request.userAnswers.set(CharityBeneficiaryStatus(index), Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _ <- repository.set(updatedAnswers)
      } yield Redirect(controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId))
  }
}

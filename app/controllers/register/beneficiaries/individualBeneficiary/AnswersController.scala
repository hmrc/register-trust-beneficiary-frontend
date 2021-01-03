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

package controllers.register.beneficiaries.individualBeneficiary

import config.annotations.IndividualBeneficiary
import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import javax.inject.Inject
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.IndividualBeneficiaryStatus
import pages.register.beneficiaries.AnswersPage
import pages.register.beneficiaries.individual.NamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.answers.IndividualBeneficiaryAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.beneficiaries.individualBeneficiary.AnswersView

import scala.concurrent.{ExecutionContext, Future}

class AnswersController @Inject()(
                                   override val messagesApi: MessagesApi,
                                   registrationsRepository: RegistrationsRepository,
                                   identify: RegistrationIdentifierAction,
                                   @IndividualBeneficiary navigator: Navigator,
                                   getData: DraftIdRetrievalActionProvider,
                                   requireData: RegistrationDataRequiredAction,
                                   val controllerComponents: MessagesControllerComponents,
                                   requiredAnswer: RequiredAnswerActionProvider,
                                   view: AnswersView,
                                   countryOptions: CountryOptions
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(NamePage(index), routes.NameController.onPageLoad(index, draftId)))


  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val answers = new IndividualBeneficiaryAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          answers.individualBeneficiaryRows(index)
        )
      )

      Ok(view(index, draftId, sections))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(IndividualBeneficiaryStatus(index), Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(AnswersPage, draftId, request.userAnswers))
  }
}

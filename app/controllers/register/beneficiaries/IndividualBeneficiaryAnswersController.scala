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

package controllers.register.beneficiaries

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.NormalMode
import models.registration.pages.Status.Completed
import navigation.Navigator
import pages.entitystatus.IndividualBeneficiaryStatus
import pages.register.beneficiaries.individual.{IndividualBeneficiaryAnswersPage, IndividualBeneficiaryNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.beneficiaries.IndividualBenficiaryAnswersView

import scala.concurrent.{ExecutionContext, Future}

class IndividualBeneficiaryAnswersController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        registrationsRepository: RegistrationsRepository,
                                                        identify: RegistrationIdentifierAction,
                                                        navigator: Navigator,
                                                        getData: DraftIdRetrievalActionProvider,
                                                        requireData: RegistrationDataRequiredAction,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        requiredAnswer: RequiredAnswerActionProvider,
                                                        validateIndex : IndexActionFilterProvider,
                                                        view: IndividualBenficiaryAnswersView,
                                                        countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  private def actions(index : Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      requiredAnswer(RequiredAnswer(IndividualBeneficiaryNamePage(index), routes.IndividualBeneficiaryNameController.onPageLoad(NormalMode, 0, draftId)))



  def onPageLoad(index : Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val answers = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            answers.individualBeneficiaryName(index),
            answers.individualBeneficiaryDateOfBirthYesNo(index),
            answers.individualBeneficiaryDateOfBirth(index),
            answers.individualBeneficiaryIncomeYesNo(index),
            answers.individualBeneficiaryIncome(index),
            answers.individualBeneficiaryNationalInsuranceYesNo(index),
            answers.individualBeneficiaryNationalInsuranceNumber(index),
            answers.individualBeneficiaryAddressYesNo(index),
            answers.individualBeneficiaryAddressUKYesNo(index),
            answers.individualBeneficiaryAddressUK(index),
            answers.individualBeneficiaryVulnerableYesNo(index)
          ).flatten
        )
      )

      Ok(view(index, draftId, sections))
  }

  def onSubmit(index : Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(IndividualBeneficiaryStatus(index), Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(IndividualBeneficiaryAnswersPage, NormalMode, draftId)(request.userAnswers))
  }
}

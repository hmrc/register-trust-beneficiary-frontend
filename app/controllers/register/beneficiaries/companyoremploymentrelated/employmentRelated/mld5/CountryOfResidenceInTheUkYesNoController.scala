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

package controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5

import cats.data.EitherT
import config.annotations.EmploymentRelatedBeneficiary
import controllers.actions._
import controllers.actions.register.employmentRelated.NameRequiredAction
import errors.TrustErrors
import forms.YesNoFormProvider
import navigation.Navigator
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.CountryOfResidenceInTheUkYesNoPage
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.CountryOfResidenceInTheUkYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceInTheUkYesNoController @Inject()(
                                               val controllerComponents: MessagesControllerComponents,
                                               @EmploymentRelatedBeneficiary navigator: Navigator,
                                               standardActionSets: StandardActionSets,
                                               formProvider: YesNoFormProvider,
                                               view: CountryOfResidenceInTheUkYesNoView,
                                               repository: RegistrationsRepository,
                                               nameAction: NameRequiredAction,
                                               technicalErrorView: TechnicalErrorView
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("employmentRelatedBeneficiary.5mld.countryOfResidenceInTheUkYesNo")

  private def actions(draftId: String, index: Int) =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(draftId, index) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CountryOfResidenceInTheUkYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm,  draftId , index, request.beneficiaryName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(draftId, index).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId , index, request.beneficiaryName))),

        value => {
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CountryOfResidenceInTheUkYesNoPage(index), value)))
            _ <- EitherT.right[TrustErrors](repository.set(updatedAnswers))
          } yield Redirect(navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_) => InternalServerError(technicalErrorView())
          }
        }
      )
  }
}

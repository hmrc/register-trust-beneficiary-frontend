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

package controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated

import cats.data.EitherT
import config.annotations.EmploymentRelatedBeneficiary
import controllers.actions.StandardActionSets
import errors.TrustErrors
import forms.StringFormProvider
import navigation.Navigator
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.LargeBeneficiaryNamePage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.companyoremploymentrelated.employmentRelated.NameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NameController @Inject()(
                                val controllerComponents: MessagesControllerComponents,
                                standardActionSets: StandardActionSets,
                                formProvider: StringFormProvider,
                                view: NameView,
                                repository: RegistrationsRepository,
                                @EmploymentRelatedBeneficiary navigator: Navigator,
                                technicalErrorView: TechnicalErrorView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[String] = formProvider.withPrefix("employmentRelatedBeneficiary.name", 105)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(LargeBeneficiaryNamePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, index, draftId))

  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, index, draftId))),

        value => {
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(LargeBeneficiaryNamePage(index), value)))
            _ <- EitherT.right[TrustErrors](repository.set(updatedAnswers))
          } yield Redirect(navigator.nextPage(LargeBeneficiaryNamePage(index), draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_) => InternalServerError(technicalErrorView())
          }
        }
      )
  }
}

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

package controllers.register.beneficiaries.individualBeneficiary.mld5

import cats.data.EitherT
import config.annotations.IndividualBeneficiary
import controllers.actions._
import controllers.actions.register.individual.NameRequiredAction
import errors.TrustErrors
import forms.YesNoFormProvider
import navigation.Navigator
import pages.register.beneficiaries.individual.mld5.CountryOfNationalityYesNoPage
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.individualBeneficiary.mld5.CountryOfNationalityYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfNationalityYesNoController @Inject()(
                                                   val controllerComponents: MessagesControllerComponents,
                                                   repository: RegistrationsRepository,
                                                   @IndividualBeneficiary navigator: Navigator,
                                                   standardActionSets: StandardActionSets,
                                                   nameAction: NameRequiredAction,
                                                   formProvider: YesNoFormProvider,
                                                   view: CountryOfNationalityYesNoView,
                                                   technicalErrorView: TechnicalErrorView
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("individualBeneficiary.5mld.countryOfNationalityYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(CountryOfNationalityYesNoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, index, request.beneficiaryName))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
      implicit request =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.beneficiaryName))),

          value => {
            val result = for {
              updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CountryOfNationalityYesNoPage(index), value)))
              _ <- EitherT.right[TrustErrors](repository.set(updatedAnswers))
            } yield Redirect(navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, updatedAnswers))

            result.value.map {
              case Right(call) => call
              case Left(_) => InternalServerError(technicalErrorView())
            }
          }
        )
    }
}

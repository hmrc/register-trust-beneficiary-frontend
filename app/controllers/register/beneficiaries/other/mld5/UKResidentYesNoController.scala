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

package controllers.register.beneficiaries.other.mld5

import cats.data.EitherT
import cats.implicits._
import config.annotations.OtherBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.register.other.DescriptionRequiredAction
import errors.TrustErrors
import forms.YesNoFormProvider
import navigation.Navigator
import pages.register.beneficiaries.other.mld5.UKResidentYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.other.mld5.UKResidentYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UKResidentYesNoController @Inject()(
                                           val controllerComponents: MessagesControllerComponents,
                                           standardActionSets: StandardActionSets,
                                           formProvider: YesNoFormProvider,
                                           view: UKResidentYesNoView,
                                           repository: RegistrationsRepository,
                                           @OtherBeneficiary navigator: Navigator,
                                           descriptionAction: DescriptionRequiredAction,
                                           technicalErrorView: TechnicalErrorView
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("otherBeneficiary.ukResidentYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(descriptionAction(index)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(UKResidentYesNoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, index, draftId, request.description))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(descriptionAction(index)).async {
      implicit request =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, index, draftId, request.description))),

          value => {
            val result = for {
              updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(UKResidentYesNoPage(index), value)))
              _ <- EitherT.right[TrustErrors](repository.set(updatedAnswers))
            } yield Redirect(navigator.nextPage(UKResidentYesNoPage(index), draftId, updatedAnswers))

            result.value.map {
              case Right(call) => call
              case Left(_) => InternalServerError(technicalErrorView())
            }
          }
        )
    }
}

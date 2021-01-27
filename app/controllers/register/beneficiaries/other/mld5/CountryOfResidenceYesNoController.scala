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

package controllers.register.beneficiaries.other.mld5

import config.annotations.OtherBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.register.other.DescriptionRequiredAction
import forms.YesNoFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.beneficiaries.other.AddressUKYesNoPage
import pages.register.beneficiaries.other.mld5.CountryOfResidenceYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries.other.mld5.CountryOfResidenceYesNoView
import play.api.data.Form

import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceYesNoController @Inject()(
                                                  val controllerComponents: MessagesControllerComponents,
                                                  standardActionSets: StandardActionSets,
                                                  formProvider: YesNoFormProvider,
                                                  view: CountryOfResidenceYesNoView,
                                                  repository: RegistrationsRepository,
                                                  @OtherBeneficiary navigator: Navigator,
                                                  descriptionAction: DescriptionRequiredAction
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("countryOfResidenceYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(descriptionAction(index)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CountryOfResidenceYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.description, index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(descriptionAction(index)).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.description, index, draftId))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfResidenceYesNoPage(index), value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, updatedAnswers))
      )
  }
}
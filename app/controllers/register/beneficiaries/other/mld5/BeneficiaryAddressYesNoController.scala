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
import navigation.Navigator
import pages.register.beneficiaries.other.AddressUKYesNoPage
import repositories.RegistrationsRepository
import views.register.beneficiaries.other.mld5.BeneficiaryAddressYesNoView

import scala.concurrent.{ExecutionContext, Future}

class BeneficiaryAddressYesNoController @Inject()(
                                                  val controllerComponents: MessagesControllerComponents,
                                                  standardActionSets: StandardActionSets,
                                                  formProvider: YesNoFormProvider,
                                                  view: BeneficiaryAddressYesNoView,
                                                  repository: RegistrationsRepository,
                                                  @OtherBeneficiary navigator: Navigator,
                                                  descriptionAction: DescriptionRequiredAction
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("otherBeneficiary")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(descriptionAction(index)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddressUKYesNoPage(index)) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressUKYesNoPage(index), value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddressUKYesNoPage(index), draftId, updatedAnswers))
      )
  }
}

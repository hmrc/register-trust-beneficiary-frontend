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

package controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated

import config.annotations.EmploymentRelatedBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.register.employmentRelated.NameRequiredAction
import forms.YesNoFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.beneficiaries.large.LargeBeneficiaryAddressUKYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.beneficiaries.companyoremploymentrelated.employmentRelated.AddressUkYesNoView

import scala.concurrent.{ExecutionContext, Future}

class AddressUkYesNoController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          standardActionSets: StandardActionSets,
                                          formProvider: YesNoFormProvider,
                                          view: AddressUkYesNoView,
                                          repository: RegistrationsRepository,
                                          @EmploymentRelatedBeneficiary navigator: Navigator,
                                          nameAction: NameRequiredAction
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("employmentRelatedBeneficiary.addressUkYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(LargeBeneficiaryAddressUKYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, request.beneficiaryName, index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, request.beneficiaryName, index, draftId))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(LargeBeneficiaryAddressUKYesNoPage(index), value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(LargeBeneficiaryAddressUKYesNoPage(index), draftId, updatedAnswers))
      )
  }
}
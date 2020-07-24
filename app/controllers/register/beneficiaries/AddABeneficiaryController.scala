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

import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.{Enumerable, Mode}
import navigation.Navigator
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddABeneficiaryViewHelper
import views.html.register.beneficiaries.{AddABeneficiaryView, AddABeneficiaryYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           navigator: Navigator,
                                           identify: RegistrationIdentifierAction,
                                           getData: DraftIdRetrievalActionProvider,
                                           requireData: RegistrationDataRequiredAction,
                                           addAnotherFormProvider: AddABeneficiaryFormProvider,
                                           yesNoFormProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           addAnotherView: AddABeneficiaryView,
                                           yesNoView: AddABeneficiaryYesNoView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private val addAnotherForm = addAnotherFormProvider()

  private val yesNoForm = yesNoFormProvider.withPrefix("addABeneficiaryYesNo")

  private def routes(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  // TODO - remove
  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case size => Messages("addABeneficiary.count.heading", size)
    }
  }

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = routes(draftId) {
    implicit request =>

      val beneficiaries = new AddABeneficiaryViewHelper(request.userAnswers, draftId).rows
      val count = beneficiaries.count

      if(beneficiaries.count > 0) {
        Ok(addAnotherView(addAnotherForm, mode, draftId, beneficiaries.inProgress, beneficiaries.complete, heading(count)))
      } else {
        Ok(yesNoView(yesNoForm, mode, draftId))
      }
  }

  def submitOne(mode: Mode, draftId : String) : Action[AnyContent] = routes(draftId).async {
    implicit request =>
      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(
            BadRequest(yesNoView(formWithErrors, mode, draftId))
          )
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddABeneficiaryYesNoPage, mode, draftId, updatedAnswers))
        }
      )
  }

  def submitAnother(mode: Mode, draftId: String): Action[AnyContent] = routes(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          val beneficiaries = new AddABeneficiaryViewHelper(request.userAnswers, draftId).rows

          val count = beneficiaries.count
          Future.successful(BadRequest(addAnotherView(formWithErrors, mode, draftId, beneficiaries.inProgress, beneficiaries.complete, heading(count))))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddABeneficiaryPage, mode, draftId, updatedAnswers))
        }
      )
  }

}

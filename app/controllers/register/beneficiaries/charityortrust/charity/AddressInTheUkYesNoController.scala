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

package controllers.register.beneficiaries.charityortrust.charity

import config.annotations.CharityBeneficiary
import controllers.actions._
import controllers.actions.register.charity.NameRequiredAction
import forms.YesNoFormProvider
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.charity.{AddressInTheUkYesNoPage, CharityNamePage}
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries.charityortrust.charity.AddressInTheUkYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressInTheUkYesNoController @Inject()(
                                               val controllerComponents: MessagesControllerComponents,
                                               @CharityBeneficiary navigator: Navigator,
                                               standardActionSets: StandardActionSets,
                                               formProvider: YesNoFormProvider,
                                               view: AddressInTheUkYesNoView,
                                               repository: RegistrationsRepository,
                                               nameAction: NameRequiredAction
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("charity.addressInTheUkYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get

      val preparedForm = request.userAnswers.get(AddressInTheUkYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm,  draftId , index, charityName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId , index, charityName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressInTheUkYesNoPage(index), value))
            _              <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddressInTheUkYesNoPage(index), draftId, updatedAnswers))
      )
  }
}

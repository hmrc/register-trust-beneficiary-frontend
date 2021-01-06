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
import controllers.actions.register.trust.NameRequiredAction
import forms.UKAddressFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.charity.{CharityAddressUKPage, CharityNamePage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries.charityortrust.charity.CharityAddressUKView

import scala.concurrent.{ExecutionContext, Future}

class CharityAddressUKController @Inject()(
                                            val controllerComponents: MessagesControllerComponents,
                                            repository: RegistrationsRepository,
                                            @CharityBeneficiary navigator: Navigator,
                                            standardActionSets: StandardActionSets,
                                            nameAction: NameRequiredAction,
                                            formProvider: UKAddressFormProvider,
                                            view: CharityAddressUKView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get

      val preparedForm = request.userAnswers.get(CharityAddressUKPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, charityName, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
    implicit request =>

      val charityName = request.userAnswers.get(CharityNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, charityName, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CharityAddressUKPage(index), value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CharityAddressUKPage(index), draftId, updatedAnswers))
        }
      )
  }
}

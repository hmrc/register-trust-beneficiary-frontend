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

package controllers.register.beneficiaries.charityortrust.charity

import cats.data.EitherT
import config.annotations.CharityBeneficiary
import controllers.actions._
import controllers.actions.register.charity.NameRequiredAction
import errors.TrustErrors
import forms.UKAddressFormProvider
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.charity.{CharityAddressUKPage, CharityNamePage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.charity.CharityAddressUKView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CharityAddressUKController @Inject()(
                                            val controllerComponents: MessagesControllerComponents,
                                            repository: RegistrationsRepository,
                                            @CharityBeneficiary navigator: Navigator,
                                            standardActionSets: StandardActionSets,
                                            nameAction: NameRequiredAction,
                                            formProvider: UKAddressFormProvider,
                                            view: CharityAddressUKView,
                                            technicalErrorView: TechnicalErrorView
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
          val result = for {
            updatedAnswers <- EitherT(Future.successful(request.userAnswers.set(CharityAddressUKPage(index), value)))
            _ <- EitherT.right[TrustErrors](repository.set(updatedAnswers))
          } yield Redirect(navigator.nextPage(CharityAddressUKPage(index), draftId, updatedAnswers))

          result.value.map {
            case Right(call) => call
            case Left(_) => InternalServerError(technicalErrorView())
          }
        }
      )
  }
}

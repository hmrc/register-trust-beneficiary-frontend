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

package controllers.register.beneficiaries.charityortrust.trust.nonTaxable

import controllers.actions.StandardActionSets
import controllers.actions.register.company.NameRequiredAction
import forms.CountryFormProvider
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.trust.NamePage
import pages.register.beneficiaries.charityortrust.trust.nonTaxable.CountryOfResidencePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.beneficiaries.charityortrust.trust.nonTaxable.CountryOfResidenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     registrationsRepository: RegistrationsRepository,
                                                     navigator: Navigator,
                                                     standardActionSets: StandardActionSets,
                                                     nameAction: NameRequiredAction,
                                                     formProvider: CountryFormProvider,
                                                     standardActions: StandardActionSets,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: CountryOfResidenceView,
                                                     val countryOptions: CountryOptionsNonUK
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) = standardActions.identifiedUserWithData(draftId)

  private val form: Form[String] = formProvider.withPrefix("trust.nonTaxable.countryOfResidence")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val trustName = request.userAnswers.get(NamePage(index)).get

      val preparedForm = request.userAnswers.get(CountryOfResidencePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, draftId, index, trustName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val trustName = request.userAnswers.get(NamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, draftId, index, trustName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfResidencePage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfResidencePage(index), draftId, updatedAnswers))
        }
      )
  }
}

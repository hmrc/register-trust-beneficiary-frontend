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

package controllers.register.beneficiaries.classofbeneficiaries

import config.annotations.ClassOfBeneficiaries
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.ClassBeneficiaryDescriptionFormProvider
import javax.inject.Inject
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.ClassBeneficiaryStatus
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionView

import scala.concurrent.{ExecutionContext, Future}

class ClassBeneficiaryDescriptionController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       registrationsRepository: RegistrationsRepository,
                                                       @ClassOfBeneficiaries navigator: Navigator,
                                                       identify: RegistrationIdentifierAction,
                                                       getData: DraftIdRetrievalActionProvider,
                                                       requireData: RegistrationDataRequiredAction,
                                                       formProvider: ClassBeneficiaryDescriptionFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: ClassBeneficiaryDescriptionView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) = identify andThen getData(draftId) andThen requireData

  private val form = formProvider()

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ClassBeneficiaryDescriptionPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index))),

        value => {

          val answers = request.userAnswers.set(ClassBeneficiaryDescriptionPage(index), value)
            .flatMap(_.set(ClassBeneficiaryStatus(index), Completed))

          for {
            updatedAnswers <- Future.fromTry(answers)
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ClassBeneficiaryDescriptionPage(index), draftId, updatedAnswers))
        }
      )
  }
}

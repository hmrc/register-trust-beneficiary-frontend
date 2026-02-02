/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.register.beneficiaries.other

import controllers.RemoveIndexController
import controllers.actions._
import controllers.actions.register.{
  DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction
}
import forms.RemoveIndexFormProvider
import models.requests.RegistrationDataRequest
import pages.QuestionPage
import pages.register.beneficiaries.other.DescriptionPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{ActionBuilder, AnyContent, Call, MessagesControllerComponents}
import queries.{RemoveOtherBeneficiaryQuery, Settable}
import repositories.RegistrationsRepository
import views.html.{RemoveIndexView, TechnicalErrorView}
import javax.inject.Inject

import scala.concurrent.ExecutionContext

class RemoveOtherBeneficiaryController @Inject() (
  override val messagesApi: MessagesApi,
  override val registrationsRepository: RegistrationsRepository,
  identify: RegistrationIdentifierAction,
  getData: DraftIdRetrievalActionProvider,
  requireData: RegistrationDataRequiredAction,
  val formProvider: RemoveIndexFormProvider,
  val controllerComponents: MessagesControllerComponents,
  val removeView: RemoveIndexView,
  require: RequiredAnswerActionProvider,
  val technicalErrorView: TechnicalErrorView
)(implicit executionContext: ExecutionContext)
    extends RemoveIndexController {

  val ec: ExecutionContext = executionContext

  override val messagesPrefix: String = "removeOtherBeneficiaryYesNo"

  override def page(index: Int): QuestionPage[String] = DescriptionPage(index)

  override def actions(draftId: String, index: Int): ActionBuilder[RegistrationDataRequest, AnyContent] =
    identify andThen getData(draftId) andThen requireData

  override def redirect(draftId: String): Call =
    controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)

  override def formRoute(draftId: String, index: Int): Call =
    controllers.register.beneficiaries.other.routes.RemoveOtherBeneficiaryController.onSubmit(index, draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveOtherBeneficiaryQuery(index)

  override def content(index: Int)(implicit request: RegistrationDataRequest[AnyContent]): String =
    request.userAnswers.get(page(index)).getOrElse(Messages(s"$messagesPrefix.default"))

}

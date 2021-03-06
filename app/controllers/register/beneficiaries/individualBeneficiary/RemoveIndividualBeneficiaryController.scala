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

package controllers.register.beneficiaries.individualBeneficiary

import controllers.RemoveIndexController
import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.core.pages.FullName
import models.requests.RegistrationDataRequest
import pages.QuestionPage
import pages.register.beneficiaries.individual.NamePage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import queries.{RemoveIndividualBeneficiaryQuery, Settable}
import repositories.RegistrationsRepository
import views.html.RemoveIndexView

class RemoveIndividualBeneficiaryController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       override val registrationsRepository: RegistrationsRepository,
                                                       identify: RegistrationIdentifierAction,
                                                       getData: DraftIdRetrievalActionProvider,
                                                       requireData: RegistrationDataRequiredAction,
                                                       val formProvider: RemoveIndexFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       val removeView: RemoveIndexView,
                                                       require: RequiredAnswerActionProvider
                                 ) extends RemoveIndexController {

  override val messagesPrefix : String = "removeIndividualBeneficiary"

  override def page(index: Int): QuestionPage[FullName] = NamePage(index)

  override def actions(draftId : String, index: Int) =
    identify andThen getData(draftId) andThen requireData

  override def redirect(draftId : String) : Call =
    controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)

  override def formRoute(draftId: String, index: Int): Call =
    routes.RemoveIndividualBeneficiaryController.onSubmit(index, draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveIndividualBeneficiaryQuery(index)

  override def content(index: Int)(implicit request: RegistrationDataRequest[AnyContent]) : String =
    request.userAnswers.get(page(index)).map(_.toString).getOrElse(Messages(s"$messagesPrefix.default"))

}

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

package controllers.register.beneficiaries.charityortrust.trust

import controllers.RemoveIndexController
import controllers.actions._
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.QuestionPage
import pages.register.beneficiaries.charityortrust.trust.NamePage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Call, MessagesControllerComponents}
import queries.{RemoveTrustBeneficiaryQuery, Settable}
import repositories.RegistrationsRepository
import views.html.RemoveIndexView

class RemoveTrustBeneficiaryController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  override val registrationsRepository: RegistrationsRepository,
                                                  standardActionSets: StandardActionSets,
                                                  val formProvider: RemoveIndexFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  val removeView: RemoveIndexView
                                                ) extends RemoveIndexController {

  override val messagesPrefix: String = "removeTrustBeneficiaryYesNo"

  override def page(index: Int): QuestionPage[String] = NamePage(index)

  override def actions(draftId: String, index: Int) =
    standardActionSets.identifiedUserWithData(draftId)

  override def redirect(draftId: String): Call =
    controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId)

  override def formRoute(draftId: String, index: Int): Call =
    routes.RemoveTrustBeneficiaryController.onSubmit(index, draftId)

  override def removeQuery(index: Int): Settable[_] = RemoveTrustBeneficiaryQuery(index)

  override def content(index: Int)(implicit request: RegistrationDataRequest[AnyContent]): String =
    request.userAnswers.get(page(index)).getOrElse(Messages(s"$messagesPrefix.default"))

}

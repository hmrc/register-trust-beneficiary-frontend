/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.actions.register.other

import models.requests.RegistrationDataRequest
import pages.register.beneficiaries.other.DescriptionPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.ActionTransformer

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DescriptionRequiredActionAction @Inject() (index: Int)(implicit
  val executionContext: ExecutionContext,
  val messagesApi: MessagesApi
) extends ActionTransformer[RegistrationDataRequest, DescriptionRequest] with I18nSupport {

  override protected def transform[A](request: RegistrationDataRequest[A]): Future[DescriptionRequest[A]] =
    Future.successful(DescriptionRequest[A](request, getDescription(request)))

  private def getDescription[A](request: RegistrationDataRequest[A]): String =
    request.userAnswers.get(DescriptionPage(index)) match {
      case Some(description) => description
      case None              => request.messages(messagesApi)("otherBeneficiary.description.default")
    }

}

class DescriptionRequiredAction @Inject() ()(implicit
  val executionContext: ExecutionContext,
  val messagesApi: MessagesApi
) {
  def apply(index: Int): DescriptionRequiredActionAction = new DescriptionRequiredActionAction(index)
}

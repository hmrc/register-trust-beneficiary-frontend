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

package controllers.actions.register

import models.requests.{IdentifierRequest, OptionalRegistrationDataRequest}
import play.api.mvc.ActionTransformer
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftIdDataRetrievalActionProviderImpl @Inject()(registrationsRepository: RegistrationsRepository, executionContext: ExecutionContext)
  extends DraftIdRetrievalActionProvider {

  def apply(draftId: String): DraftIdDataRetrievalAction =
    new DraftIdDataRetrievalAction(draftId, registrationsRepository, executionContext)

}

trait DraftIdRetrievalActionProvider {

  def apply(draftId : String) : DraftIdDataRetrievalAction

}

class DraftIdDataRetrievalAction(
                                  draftId : String,
                                  registrationsRepository: RegistrationsRepository,
                                  implicit protected val executionContext: ExecutionContext
                                )
  extends ActionTransformer[IdentifierRequest, OptionalRegistrationDataRequest] {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalRegistrationDataRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    registrationsRepository.get(draftId).value.map {
      eitherResult =>
        val optionalUserAnswers = eitherResult.toOption.flatten
        OptionalRegistrationDataRequest(request.request, request.identifier, Session.id(hc), optionalUserAnswers, request.affinityGroup,
          request.enrolments, request.agentARN)
    }
  }

}

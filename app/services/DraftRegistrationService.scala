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

package services

import cats.data.EitherT
import config.FrontendAppConfig
import connectors.SubmissionDraftConnector
import play.api.Logging
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TrustEnvelope.TrustEnvelope

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DraftRegistrationService @Inject()(config: FrontendAppConfig,
                                         submissionDraftConnector: SubmissionDraftConnector)
                                        (implicit ec: ExecutionContext) extends Logging {


  def retrieveSettlorNinos(draftId: String)(implicit hc: HeaderCarrier): TrustEnvelope[String] = EitherT {
    submissionDraftConnector.getDraftSection(draftId, config.repositoryKeySettlors).value.map {
      case Left(value) => Left(value)
      case Right(response) => Right(
          response.data.\("data").\("settlors").\("deceased").asOpt[JsObject].getOrElse(JsObject.empty).\("nationalInsuranceNumber").asOpt[String].getOrElse("")
      )
    }
  }
}

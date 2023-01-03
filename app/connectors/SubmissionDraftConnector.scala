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

package connectors

import cats.data.EitherT
import config.FrontendAppConfig
import errors.{ServerError, TrustErrors}
import models._
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException, HttpResponse, UpstreamErrorResponse}
import utils.TrustEnvelope.TrustEnvelope

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClient, config: FrontendAppConfig) extends Logging {

  private val className: String = getClass.getName

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  def setDraftSectionSet(draftId: String, section: String, data: RegistrationSubmission.DataSet)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[HttpResponse] = {
    EitherT {
      http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/set/$section", Json.toJson(data)).map(Right(_)).recover {
        case ex: HttpException =>
          logger.error(s"[$className][setDraftSectionSet] Error with status: ${ex.responseCode}, and message: ${ex.message}")
          Left(ServerError())
        case ex: UpstreamErrorResponse =>
          logger.error(s"[$className][setDraftSectionSet] Error with status: ${ex.statusCode}, and message: ${ex.message}")
          Left(ServerError())
      }
    }
  }

  def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[SubmissionDraftResponse] = {
    EitherT {
      http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/$section").map(Right(_)).recover {
        case ex: HttpException =>
          logger.error(s"[$className][getDraftSection] Error with status: ${ex.responseCode}, and message: ${ex.message}")
          Left(ServerError())
        case ex: UpstreamErrorResponse =>
          logger.error(s"[$className][getDraftSection] Error with status: ${ex.statusCode}, and message: ${ex.message}")
          Left(ServerError())
      }
    }
  }

  // TODO - once the trust matching journey has been fixed to set a value for trustTaxable the recover can be removed
  def getIsTrustTaxable(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[Boolean] = {
    EitherT[Future, TrustErrors, Boolean] {
      http.GET[Boolean](s"$submissionsBaseUrl/$draftId/is-trust-taxable").map(Right(_)).recover {
        case _ => Right(true) //TODO find out why this returns a Right()
      }
    }
  }
}

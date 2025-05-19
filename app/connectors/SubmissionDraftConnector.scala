/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import utils.TrustEnvelope.TrustEnvelope

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class SubmissionDraftConnector @Inject()(http: HttpClientV2, config: FrontendAppConfig) extends ConnectorErrorResponseHandler {

  override val className: String = getClass.getName

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  def setDraftSectionSet(draftId: String, section: String, data: RegistrationSubmission.DataSet)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[Boolean] = {
    EitherT {
      http
        .post(url"$submissionsBaseUrl/$draftId/set/$section")
        .withBody(Json.toJson(data))
        .execute[HttpResponse]
        .map(response => {
          response.status match {
            case OK => Right(true)
            case status => Left(handleError(status, "setDraftSectionSet"))
          }
        })
        .recover {
          case ex => Left(handleError(ex, "setDraftSectionSet"))
        }
    }
  }

  def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[SubmissionDraftResponse] = {
    EitherT {
      http
        .get(url"$submissionsBaseUrl/$draftId/$section")
        .execute[HttpResponse]
        .map(response =>
          response.status match {
            case OK =>
              Try(response.json.as[SubmissionDraftResponse]) match {
                case Success(submissionDraftResponse) => Right(submissionDraftResponse)
                case Failure(e) =>
                  logger.error(s"[$className][getDraftSection] Error parsing JSON, status: ${response.status}, " +
                    s"and body: ${response.body}, exception: ${e.getMessage}")

                  Left(ServerError())
              }
            case _ =>
              logger.error(s"[$className][getDraftSection] Error with status: ${response.status}, and body: ${response.body}")
              Left(ServerError())
          }
        )
    }
  }

  def getIsTrustTaxable(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[Boolean] = {

    EitherT[Future, TrustErrors, Boolean] {
      http
        .get(url"$submissionsBaseUrl/$draftId/is-trust-taxable")
        .execute[Boolean]
        .map(Right(_))
        .recover {
          case _ => Right(true)
        }
    }
  }
}

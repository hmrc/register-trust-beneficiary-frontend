/*
 * Copyright 2022 HM Revenue & Customs
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
import errors.ServerError
import models.TaskStatus.TaskStatus
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException, HttpResponse, UpstreamErrorResponse}
import utils.TrustEnvelope.TrustEnvelope

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TrustsStoreConnector @Inject()(http: HttpClient, config: FrontendAppConfig) extends Logging {

  private val className: String = getClass.getName

  private val baseUrl: String = s"${config.trustsStoreUrl}/trusts-store"

  def updateTaskStatus(identifier: String, taskStatus: TaskStatus)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): TrustEnvelope[HttpResponse] = {
    EitherT {
      val url: String = s"$baseUrl/register/tasks/update-beneficiaries/$identifier"
      http.POST[TaskStatus, HttpResponse](url, taskStatus).map(Right(_)).recover {
        case ex: HttpException =>
          logger.error(s"[$className][updateTaskStatus] Error with status: ${ex.responseCode}, and message: ${ex.message}")
          Left(ServerError())
        case ex: UpstreamErrorResponse =>
          logger.error(s"[$className][updateTaskStatus] Error with status: ${ex.statusCode}, and message: ${ex.message}")
          Left(ServerError())
      }
    }
  }
}


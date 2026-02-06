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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import errors.ServerError
import models.TaskStatus
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK, UNAUTHORIZED}
import play.api.inject.guice.GuiceApplicationBuilder
import utils.WireMockHelper

class TrustsStoreConnectorSpec extends SpecBase with WireMockHelper {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq("microservice.services.trusts-store.port" -> server.port(), "auditing.enabled" -> false): _*)
    .build()

  ".updateTaskStatus" must {

    val url = s"/trusts-store/register/tasks/update-beneficiaries/$fakeDraftId"

    "return OK with the current task status" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts-store.port" -> server.port(),
            "auditing.enabled"                        -> false
          ): _*
        )
        .build()

      val connector = application.injector.instanceOf[TrustsStoreConnector]

      server.stubFor(
        post(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK))
      )

      val futureResult = connector.updateTaskStatus(fakeDraftId, TaskStatus.Completed)

      whenReady(futureResult.value) { r =>
        r mustBe Right(true)
      }

      application.stop()
    }

    Seq(INTERNAL_SERVER_ERROR, BAD_REQUEST, UNAUTHORIZED, NOT_FOUND).foreach(errorStatus =>
      s"return default tasks when a failure occurs (e.g. ${errorStatus.toString})" in {

        val application = applicationBuilder()
          .configure(
            Seq(
              "microservice.services.trusts-store.port" -> server.port(),
              "auditing.enabled"                        -> false
            ): _*
          )
          .build()

        val connector = application.injector.instanceOf[TrustsStoreConnector]

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(errorStatus))
        )

        connector.updateTaskStatus(fakeDraftId, TaskStatus.Completed) map { response =>
          response mustBe Left(ServerError())
        }

        application.stop()
      }
    )
  }

}

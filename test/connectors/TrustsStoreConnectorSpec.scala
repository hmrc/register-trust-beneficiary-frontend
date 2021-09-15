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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import models.TaskStatus
import org.scalatest.{MustMatchers, OptionValues}
import utils.WireMockHelper

class TrustsStoreConnectorSpec extends SpecBase with MustMatchers with OptionValues with WireMockHelper {

  ".updateTaskStatus" must {

    val url = s"/trusts-store/register/tasks/update-beneficiaries/$fakeDraftId"

    "return OK with the current task status" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsStoreConnector]

      server.stubFor(
        post(urlEqualTo(url))
          .willReturn(ok())
      )

      val futureResult = connector.updateTaskStatus(fakeDraftId, TaskStatus.Completed)

      whenReady(futureResult) {
        r =>
          r.status mustBe 200
      }

      application.stop()
    }

    "return default tasks when a failure occurs" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.trusts-store.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      val connector = application.injector.instanceOf[TrustsStoreConnector]

      server.stubFor(
        post(urlEqualTo(url))
          .willReturn(serverError())
      )

      connector.updateTaskStatus(fakeDraftId, TaskStatus.Completed) map { response =>
        response.status mustBe 500
      }

      application.stop()
    }
  }

}

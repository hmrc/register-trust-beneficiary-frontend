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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import errors.ServerError
import models.{RegistrationSubmission, SubmissionDraftResponse}
import org.scalatest.EitherValues
import play.api.Application
import play.api.http.Status
import play.api.http.Status.{BAD_GATEWAY, BAD_REQUEST, CONFLICT, INTERNAL_SERVER_ERROR, METHOD_NOT_ALLOWED, SERVICE_UNAVAILABLE, UNAUTHORIZED}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsBoolean, Json}
import play.api.test.Helpers.CONTENT_TYPE
import utils.WireMockHelper

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SubmissionDraftConnectorSpec extends SpecBase with WireMockHelper with EitherValues {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val connector = injector.instanceOf[SubmissionDraftConnector]

  private val testDraftId = "draftId"
  private val testSection = "section"
  private val submissionsUrl = s"/trusts/register/submission-drafts"
  private val submissionUrl = s"$submissionsUrl/$testDraftId/$testSection"
  private val setSubmissionUrl = s"$submissionsUrl/$testDraftId/set/$testSection"

  "SubmissionDraftConnector" when {

    "submission drafts" must {

      "set data for section set" in {

        val sectionData = Json.parse(
          """
            |{
            | "field1": "value1",
            | "field2": "value2"
            |}
            |""".stripMargin)

        val submissionDraftSetData = RegistrationSubmission.DataSet(
          sectionData,
          List.empty,
          List.empty)

        server.stubFor(
          post(urlEqualTo(setSubmissionUrl))
            .withHeader(CONTENT_TYPE, containing("application/json"))
            .withRequestBody(equalTo(Json.toJson(submissionDraftSetData).toString()))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
            )
        )

        val result = Await.result(connector.setDraftSectionSet(testDraftId, testSection, submissionDraftSetData).value, Duration.Inf)
        result mustBe Right(true)
      }

      "get data for section" in {

        val draftData = Json.parse(
          """
            |{
            | "field1": "value1",
            | "field2": "value2"
            |}
            |""".stripMargin)

        val draftResponseJson =
          """
            |{
            | "createdAt": "2012-02-03T09:30:00",
            | "data": {
            |  "field1": "value1",
            |  "field2": "value2"
            | }
            |}
            |""".stripMargin

        server.stubFor(
          get(urlEqualTo(submissionUrl))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(draftResponseJson)
            )
        )

        val result: SubmissionDraftResponse = Await.result(connector.getDraftSection(testDraftId, testSection).value, Duration.Inf).value
        result.createdAt mustBe LocalDateTime.of(2012, 2, 3, 9, 30)
        result.data mustBe draftData
      }
    }

    "getIsTrustTaxable" must {

      "return true if the trust is taxable" in {
        server.stubFor(
          get(urlEqualTo(s"$submissionsUrl/$testDraftId/is-trust-taxable"))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(JsBoolean(true).toString)
            )
        )

        val result = Await.result(connector.getIsTrustTaxable(testDraftId).value, Duration.Inf)
        result mustBe Right(true)
      }

      "return false if the trust is non taxable" in {
        server.stubFor(
          get(urlEqualTo(s"$submissionsUrl/$testDraftId/is-trust-taxable"))
            .willReturn(
              aResponse()
                .withStatus(Status.OK)
                .withBody(JsBoolean(false).toString)
            )
        )

        val result: Boolean = Await.result(connector.getIsTrustTaxable(testDraftId).value, Duration.Inf).value
        result.booleanValue() mustBe false
      }

      "recover to true as default" in {
        server.stubFor(
          get(urlEqualTo(s"$submissionsUrl/$testDraftId/is-trust-taxable"))
            .willReturn(
              aResponse()
                .withStatus(Status.NOT_FOUND)
            )
        )

        val result = Await.result(connector.getIsTrustTaxable(testDraftId).value, Duration.Inf)
        result mustBe Right(true)
      }
    }

    "handling submission draft errors" must {

      Seq(SERVICE_UNAVAILABLE, BAD_REQUEST, UNAUTHORIZED, CONFLICT, INTERNAL_SERVER_ERROR, BAD_GATEWAY).foreach { httpStatus =>
        s"return Left(ServerError()) for $httpStatus" in {
          val sectionData = Json.parse(
            """
              |{
              | "field1": "value1",
              | "field2": "value2"
              |}
              |""".stripMargin)

          val submissionDraftSetData = RegistrationSubmission.DataSet(
            sectionData,
            List.empty,
            List.empty)

          server.stubFor(
            post(urlEqualTo(setSubmissionUrl))
              .withHeader(CONTENT_TYPE, containing("application/json"))
              .withRequestBody(equalTo(Json.toJson(submissionDraftSetData).toString()))
              .willReturn(
                aResponse()
                  .withStatus(httpStatus)
              )
          )

          val result = Await.result(connector.setDraftSectionSet(testDraftId, testSection, submissionDraftSetData).value, Duration.Inf)
          result mustBe Left(ServerError())
        }

      }
    }

    "handling get draft section call errors" must {

      Seq(SERVICE_UNAVAILABLE, BAD_REQUEST, METHOD_NOT_ALLOWED, INTERNAL_SERVER_ERROR, BAD_GATEWAY).foreach { httpStatus =>
        s"return Left(ServerError()) for $httpStatus" in {

          server.stubFor(
            get(urlEqualTo(submissionUrl))
              .willReturn(
                aResponse()
                  .withStatus(httpStatus)
              )
          )

          val result = Await.result(connector.getDraftSection(testDraftId, testSection).value, Duration.Inf)
          result mustBe Left(ServerError())
        }

      }
    }
  }
}

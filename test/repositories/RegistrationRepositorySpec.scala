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

package repositories

import base.SpecBase
import cats.data.EitherT
import connectors.SubmissionDraftConnector
import errors.TrustErrors
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDateTime
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RegistrationRepositorySpec extends SpecBase {

  private val unusedSubmissionSetFactory = mock[SubmissionSetFactory]

  private def createRepository(connector: SubmissionDraftConnector, submissionSetFactory: SubmissionSetFactory) =
    new DefaultRegistrationsRepository(connector, frontendAppConfig, submissionSetFactory)

  "RegistrationRepository" when {
    "getting user answers" must {
      "read answers from my section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector, unusedSubmissionSetFactory)

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(userAnswers), None)

        when(mockConnector.getDraftSection(any(), any())(any(), any()))
          .thenReturn(EitherT[Future, TrustErrors, SubmissionDraftResponse](Future.successful(Right(response))))

        val result = Await.result(repository.get(draftId).value, Duration.Inf)

        result.value mustBe Some(userAnswers)
        verify(mockConnector).getDraftSection(draftId, frontendAppConfig.repositoryKey)(hc, executionContext)
      }
      "read answers from main section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val dummyData = Json.parse("""
            |{
            | "data" : {
            |   "someField": "someValue"
            | }
            |}
            |""".stripMargin)

        val mockConnector = mock[SubmissionDraftConnector]

        val repository = createRepository(mockConnector, unusedSubmissionSetFactory)

        val response = SubmissionDraftResponse(LocalDateTime.now, Json.toJson(dummyData), None)

        when(mockConnector.getDraftSection(any(), any())(any(), any()))
          .thenReturn(EitherT[Future, TrustErrors, SubmissionDraftResponse](Future.successful(Right(response))))

        val result = Await.result(repository.getSettlorsAnswers(draftId).value, Duration.Inf)

        val expectedAnswers     = Json.obj("someField" -> "someValue")
        val expectedUserAnswers = ReadOnlyUserAnswers(expectedAnswers)

        result.value mustBe Some(expectedUserAnswers)
        verify(mockConnector).getDraftSection(draftId, "settlors")(hc, executionContext)
      }

    }

    "setting user answers" must {
      "write answers to my section" in {
        implicit lazy val hc: HeaderCarrier = HeaderCarrier()

        val draftId = "DraftId"

        val userAnswers = UserAnswers(draftId = draftId, internalAuthId = "internalAuthId")

        val mockConnector = mock[SubmissionDraftConnector]

        val submissionSet = RegistrationSubmission.DataSet(
          Json.obj(),
          List.empty,
          List.empty
        )

        val mockSubmissionSetFactory = mock[SubmissionSetFactory]
        when(mockSubmissionSetFactory.createFrom(any())(any())).thenReturn(submissionSet)

        val repository = createRepository(mockConnector, mockSubmissionSetFactory)

        when(mockConnector.setDraftSectionSet(any(), any(), any())(any(), any()))
          .thenReturn(EitherT[Future, TrustErrors, Boolean](Future.successful(Right(true))))

        val result = Await.result(repository.set(userAnswers).value, Duration.Inf)

        result.value mustBe true
        verify(mockConnector).setDraftSectionSet(draftId, frontendAppConfig.repositoryKey, submissionSet)(
          hc,
          executionContext
        )
      }
    }
  }

}

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

package base

import cats.data.EitherT
import errors.TrustErrors
import models.{ReadOnlyUserAnswers, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import repositories.RegistrationsRepository

import scala.concurrent.Future

trait Mocked extends MockitoSugar {

  val mockRegistrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]

  def mockRegistrationsRepositoryBuilder(
    getResult: Either[TrustErrors, Option[UserAnswers]] = Right(None),
    setResult: Either[TrustErrors, Boolean] = Right(true),
    getSettlorsAnswersResult: Either[TrustErrors, Option[ReadOnlyUserAnswers]] = Right(None)
  ): RegistrationsRepository = {
    when(
      mockRegistrationsRepository
        .get(any())(any())
    ).thenReturn(EitherT[Future, TrustErrors, Option[UserAnswers]](Future.successful(getResult)))

    when(
      mockRegistrationsRepository
        .set(any())(any(), any())
    ).thenReturn(EitherT[Future, TrustErrors, Boolean](Future.successful(setResult)))

    when(mockRegistrationsRepository.getSettlorsAnswers(any())(any()))
      .thenReturn(
        EitherT[Future, TrustErrors, Option[ReadOnlyUserAnswers]](Future.successful(getSettlorsAnswersResult))
      )

    mockRegistrationsRepository
  }

}

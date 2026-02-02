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

import base.SpecBase
import cats.data.EitherT
import connectors.TrustsStoreConnector
import errors.TrustErrors
import models.TaskStatus
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}

import scala.concurrent.Future

class TrustsStoreServiceSpec extends SpecBase {

  val mockConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  val trustsStoreService = new TrustsStoreService(mockConnector)

  ".updateTaskStatus" must {
    "call trusts store connector" in {

      when(mockConnector.updateTaskStatus(any(), any())(any(), any()))
        .thenReturn(EitherT[Future, TrustErrors, Boolean](Future.successful(Right(true))))

      val result = trustsStoreService.updateTaskStatus(draftId, TaskStatus.Completed)

      whenReady(result.value) { res =>
        res mustBe Right(true)
        verify(mockConnector).updateTaskStatus(eqTo(draftId), eqTo(TaskStatus.Completed))(any(), any())
      }
    }
  }

}

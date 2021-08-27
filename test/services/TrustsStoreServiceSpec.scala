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

package services

import base.SpecBase
import connectors.TrustsStoreConnector
import models.{FeatureResponse, TaskStatus}
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import play.api.http.Status.OK
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class TrustsStoreServiceSpec extends SpecBase {

  val mockConnector = mock[TrustsStoreConnector]

  val trustsStoreService = new TrustsStoreService(mockConnector)

  "is5mldEnabled" must {



    "return true when 5mld is enabled" in {

      when(mockConnector.getFeature(any())(any(), any())).thenReturn(Future.successful(FeatureResponse("5mld", isEnabled = true)))

      val result = trustsStoreService.is5mldEnabled()

       whenReady(result) { res =>
         res mustEqual true
       }
    }

    "return false when 5mld is disabled" in {

      when(mockConnector.getFeature(any())(any(), any())).thenReturn(Future.successful(FeatureResponse("5mld", isEnabled = false)))

      val result = trustsStoreService.is5mldEnabled()

      whenReady(result) { res =>
        res mustEqual false
      }
    }
  }

  ".updateTaskStatus" must {
    "call trusts store connector" in {

      when(mockConnector.updateTaskStatus(any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val result = trustsStoreService.updateTaskStatus(draftId, TaskStatus.Completed)

      whenReady(result) { res =>
        res.status mustBe OK
        verify(mockConnector).updateTaskStatus(eqTo(draftId), eqTo(TaskStatus.Completed))(any(), any())
      }
    }
  }
}
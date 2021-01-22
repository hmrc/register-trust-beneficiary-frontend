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

package controllers.register.beneficiaries

import base.SpecBase
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService
import views.html.register.beneficiaries.{NonTaxableInfoView, TaxableInfoView}

import scala.concurrent.Future

class InfoControllerSpec extends SpecBase {

  lazy val mockFeatureFlagService = mock[FeatureFlagService]

  "Info Controller" must {

    "return OK and the correct view for a GET with 5mld disabled" in {

      when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
        .thenReturn(Future.successful(false))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
        bind[FeatureFlagService].toInstance(mockFeatureFlagService)
        ).build()

      val request = FakeRequest(GET, routes.InfoController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[TaxableInfoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET with 5mld enabled" in {

      when(mockFeatureFlagService.is5mldEnabled()(any(), any()))
        .thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[FeatureFlagService].toInstance(mockFeatureFlagService)
        ).build()

      val request = FakeRequest(GET, routes.InfoController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NonTaxableInfoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId)(request, messages).toString

      application.stop()
    }
  }
}

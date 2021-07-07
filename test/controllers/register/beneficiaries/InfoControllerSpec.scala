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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.beneficiaries.{Info4mldView, Info5mldView}

class InfoControllerSpec extends SpecBase {

  "Info Controller" must {

    "return OK and the correct view for a GET with 5mld disabled" in {

      val userAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.InfoController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[Info4mldView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET with 5mld enabled" in {

      val userAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.InfoController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[Info5mldView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, isTaxable = true)(request, messages).toString

      application.stop()
    }

    "redirect to WhatTypeOfBeneficiaryController" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, routes.InfoController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[Info5mldView]

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url

      application.stop()
    }
  }
}

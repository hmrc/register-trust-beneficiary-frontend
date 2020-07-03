/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.WhatTypeOfBeneficiaryFormProvider
import models.NormalMode
import models.registration.pages.WhatTypeOfBeneficiary
import pages.register.beneficiaries.{ClassBeneficiaryDescriptionPage, WhatTypeOfBeneficiaryPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.beneficiaries.WhatTypeOfBeneficiaryView

class WhatTypeOfBeneficiaryControllerSpec extends SpecBase {

  lazy val whatTypeOfBeneficiaryRoute = routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url

  val formProvider = new WhatTypeOfBeneficiaryFormProvider()
  val form = formProvider()

  "WhatTypeOfBeneficiary Controller" must {

    "return OK and the correct view for a GET when no beneficiaries are added" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, false)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when beneficiaries are already added." in {
     val userAnswer =  emptyUserAnswers.set(ClassBeneficiaryDescriptionPage(0), "description").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswer)).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, true)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view without value on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode, fakeDraftId, false)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whatTypeOfBeneficiaryRoute)
          .withFormUrlEncodedBody(("value", WhatTypeOfBeneficiary.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whatTypeOfBeneficiaryRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId,false)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, whatTypeOfBeneficiaryRoute)
          .withFormUrlEncodedBody(("value", WhatTypeOfBeneficiary.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}

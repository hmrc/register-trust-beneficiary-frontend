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

package controllers.register.beneficiaries

import base.SpecBase
import errors.ServerError
import forms.WhatTypeOfBeneficiaryFormProvider
import models.registration.pages.WhatTypeOfBeneficiary
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.RadioOption
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.WhatTypeOfBeneficiaryView

class WhatTypeOfBeneficiaryControllerSpec extends SpecBase {

  private lazy val whatTypeOfBeneficiaryRoute: String = routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId).url

  private val formProvider = new WhatTypeOfBeneficiaryFormProvider()
  private val form: Form[WhatTypeOfBeneficiary] = formProvider()

  private val roPrefix: String = "whatTypeOfBeneficiary"

  private val defaultOptions: List[RadioOption] = List(
    RadioOption(roPrefix, WhatTypeOfBeneficiary.Individual.toString),
    RadioOption(roPrefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
    RadioOption(roPrefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
    RadioOption(roPrefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString),
    RadioOption(roPrefix, WhatTypeOfBeneficiary.Other.toString)
  )

  private val validAnswer: WhatTypeOfBeneficiary = WhatTypeOfBeneficiary.values.head

  "WhatTypeOfBeneficiary Controller" must {

    "return OK and the correct view for a GET when no beneficiaries are added" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, beneficiaryAdded = false, defaultOptions)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when beneficiaries are already added." in {
     val userAnswer =  emptyUserAnswers.set(ClassBeneficiaryDescriptionPage(0), "description").right.get

      val application = applicationBuilder(userAnswers = Some(userAnswer)).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, beneficiaryAdded = true, defaultOptions)(request, messages).toString

      application.stop()
    }

    "populate the view without value on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(WhatTypeOfBeneficiaryPage, validAnswer).right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val view = application.injector.instanceOf[WhatTypeOfBeneficiaryView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, beneficiaryAdded = false, defaultOptions)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, whatTypeOfBeneficiaryRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError())).build()

      val request =
        FakeRequest(POST, whatTypeOfBeneficiaryRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

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
        view(boundForm, fakeDraftId, beneficiaryAdded = false, defaultOptions)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, whatTypeOfBeneficiaryRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, whatTypeOfBeneficiaryRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

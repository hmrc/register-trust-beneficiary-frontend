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

package controllers.register.beneficiaries.charityortrust.trust

import base.SpecBase
import config.annotations.TrustBeneficiary
import errors.ServerError
import forms.YesNoFormProvider
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.charityortrust.trust.{AddressUKYesNoPage, NamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.trust.AddressUKYesNoView

class AddressUKYesNoControllerSpec extends SpecBase {

  private val formProvider = new YesNoFormProvider()
  private val form: Form[Boolean] = formProvider.withPrefix("trustBeneficiaryAddressUKYesNo")
  private val index: Int = 0

  private val name = "Name"

  private lazy val trustBeneficiaryAddressUKYesNoRoute: String = routes.AddressUKYesNoController.onPageLoad(index, fakeDraftId).url

  "TrustBeneficiaryAddressUKYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trustBeneficiaryAddressUKYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AddressUKYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(AddressUKYesNoPage(index), true).value
        .set(NamePage(index),name).value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trustBeneficiaryAddressUKYesNoRoute)

      val view = application.injector.instanceOf[AddressUKYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[TrustBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, trustBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[TrustBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, trustBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = app.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trustBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AddressUKYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustBeneficiaryAddressUKYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustBeneficiaryAddressUKYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

  }
}

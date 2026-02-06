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

package controllers.register.beneficiaries.charityortrust.charity

import base.SpecBase
import config.annotations.CharityBeneficiary
import errors.ServerError
import forms.InternationalAddressFormProvider
import models.core.pages.InternationalAddress
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.charityortrust.charity.{CharityInternationalAddressPage, CharityNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, _}
import utils.countryOptions.CountryOptionsNonUK
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.charity.CharityInternationalAddressView

class CharityInternationalAddressControllerSpec extends SpecBase {

  private val formProvider                     = new InternationalAddressFormProvider()
  private val form: Form[InternationalAddress] = formProvider()
  private val index: Int                       = 0

  private val charityName = "Test"

  private lazy val charityInternationalAddressRoute: String =
    routes.CharityInternationalAddressController.onPageLoad(index, fakeDraftId).url

  "CharityInternationalAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(CharityNamePage(index), "Test")
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val countryOption: CountryOptionsNonUK = application.injector.instanceOf[CountryOptionsNonUK]

      val request = FakeRequest(GET, charityInternationalAddressRoute)

      val view = application.injector.instanceOf[CharityInternationalAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOption.options(), fakeDraftId, index, charityName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val previousAnswer = InternationalAddress("line 1", "line 2", Some("line 3"), "country")

      val userAnswers = emptyUserAnswers
        .set(CharityNamePage(index), "Test")
        .value
        .set(CharityInternationalAddressPage(index), previousAnswer)
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val countryOption: CountryOptionsNonUK = application.injector.instanceOf[CountryOptionsNonUK]

      val request = FakeRequest(GET, charityInternationalAddressRoute)

      val view = application.injector.instanceOf[CharityInternationalAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(previousAnswer), countryOption.options(), fakeDraftId, index, charityName)(
          request,
          messages
        ).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(CharityNamePage(index), "Test")
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[CharityBeneficiary]).toInstance(new FakeNavigator)
          )
          .build()

      val request =
        FakeRequest(POST, charityInternationalAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "Spain"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val userAnswers = emptyUserAnswers
        .set(CharityNamePage(index), "Test")
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[CharityBeneficiary]).toInstance(new FakeNavigator)
          )
          .build()

      val request =
        FakeRequest(POST, charityInternationalAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "Spain"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(CharityNamePage(index), "Test")
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val countryOption: CountryOptionsNonUK = application.injector.instanceOf[CountryOptionsNonUK]

      val request =
        FakeRequest(POST, charityInternationalAddressRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[CharityInternationalAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOption.options(), fakeDraftId, index, charityName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, charityInternationalAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, charityInternationalAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "Italy"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

  }

}

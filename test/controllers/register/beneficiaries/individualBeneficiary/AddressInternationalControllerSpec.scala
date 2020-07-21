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

package controllers.register.beneficiaries.individualBeneficiary

import base.SpecBase
import forms.InternationalAddressFormProvider
import models.core.pages.InternationalAddress
import models.core.pages.FullName
import models.{NormalMode, UserAnswers}
import pages.register.beneficiaries.individual.{AddressInternationalPage, NamePage}
import play.api.Application
import play.api.data.Form
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils._
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.beneficiaries.individualBeneficiary.AddressInternationalView

import scala.concurrent.Future

class AddressInternationalControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new InternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider()
  val index = 0
  val name = FullName("First Name", None, "Last Name")

  lazy val addressInternationalRoute: String = routes.AddressInternationalController.onPageLoad(NormalMode, index, fakeDraftId).url


  "SettlorBusinessAddressInternational Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = emptyUserAnswers.set(NamePage(index), name).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, addressInternationalRoute)

      val view: AddressInternationalView = application.injector.instanceOf[AddressInternationalView]

      val result: Future[Result] = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, NormalMode, index, fakeDraftId, name.toString)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).success.value
        .set(AddressInternationalPage(index), InternationalAddress("line 1", "line 2", Some("line 3"), "country")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, addressInternationalRoute)

      val view = application.injector.instanceOf[AddressInternationalView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form.fill(InternationalAddress("line 1", "line 2", Some("line 3"), "country")),
          countryOptions,
          NormalMode,
          index,
          fakeDraftId,
          name.toString)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).success.value
        .set(AddressInternationalPage(index), InternationalAddress("line 1", "line 2", Some("line 3"), "country")).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, addressInternationalRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "IN"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, addressInternationalRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[AddressInternationalView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, NormalMode, index, fakeDraftId, name.toString)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, addressInternationalRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, addressInternationalRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}

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

package controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated

import base.SpecBase
import config.annotations.EmploymentRelatedBeneficiary
import errors.ServerError
import forms.InternationalAddressFormProvider
import models.core.pages.InternationalAddress
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.{LargeBeneficiaryAddressInternationalPage, LargeBeneficiaryNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.companyoremploymentrelated.employmentRelated.NonUkAddressView

class NonUkAddressControllerSpec extends SpecBase {

  private val index = 0
  private val form: Form[InternationalAddress] = new InternationalAddressFormProvider()()
  private val nonUkAddressRoute: String = routes.NonUkAddressController.onPageLoad(index, draftId).url
  private val name: String = "EmploymentRelated"
  private val onwardRoute = Call("GET", "/foo")
  private val answer = InternationalAddress("Line 1", "Line 2", None, "DE")
  private val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

  private val baseAnswers = emptyUserAnswers.set(LargeBeneficiaryNamePage(index), name).right.get

  "NonUkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, nonUkAddressRoute)

      val view = application.injector.instanceOf[NonUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, name, index, draftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = baseAnswers.set(LargeBeneficiaryAddressInternationalPage(index), answer).right.get

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, nonUkAddressRoute)

      val view = application.injector.instanceOf[NonUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(answer), countryOptions, name, index, draftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[EmploymentRelatedBeneficiary]).toInstance(new FakeNavigator(onwardRoute))
          ).build()

      val request =
        FakeRequest(POST, nonUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "DE"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[EmploymentRelatedBeneficiary]).toInstance(new FakeNavigator(onwardRoute))
          ).build()

      val request =
        FakeRequest(POST, nonUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "DE"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(POST, nonUkAddressRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NonUkAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, name, index, draftId)(request, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, nonUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, nonUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "DE"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

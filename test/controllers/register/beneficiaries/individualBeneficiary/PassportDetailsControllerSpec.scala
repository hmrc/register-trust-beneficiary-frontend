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

package controllers.register.beneficiaries.individualBeneficiary

import base.SpecBase
import config.FrontendAppConfig
import config.annotations.IndividualBeneficiary
import errors.ServerError
import forms.PassportOrIdCardFormProvider
import models.core.pages.FullName
import models.registration.pages.PassportOrIdCardDetails
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.individual.{NamePage, PassportDetailsPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.countryOptions.CountryOptions
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.individualBeneficiary.PassportDetailsView

import java.time.LocalDate

class PassportDetailsControllerSpec extends SpecBase {

  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  private val formProvider = new PassportOrIdCardFormProvider(mockFrontendAppConfig)
  private val index: Int = 0
  private val form: Form[PassportOrIdCardDetails] = formProvider("individualBeneficiaryPassportDetails", emptyUserAnswers, index)

  private val fullName: FullName = FullName("first name", None, "Last name")
  private val validData: PassportOrIdCardDetails = PassportOrIdCardDetails("country", "card number", LocalDate.of(2020, 1, 1))

  private lazy val passportDetailsControllerRoute: String = routes.PassportDetailsController.onPageLoad(index, fakeDraftId).url

  "PassportDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), fullName).right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, passportDetailsControllerRoute)

      val result = route(application, request).value

      val countryOption: CountryOptions = application.injector.instanceOf[CountryOptions]

      val view = application.injector.instanceOf[PassportDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOption.options(), fakeDraftId, index, fullName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), fullName).right.get
        .set(PassportDetailsPage(index), validData).right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, passportDetailsControllerRoute)

      val result = route(application, request).value

      val countryOption: CountryOptions = application.injector.instanceOf[CountryOptions]

      val view = application.injector.instanceOf[PassportDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validData), countryOption.options(), fakeDraftId, index, fullName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), fullName).right.get

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, passportDetailsControllerRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day" -> "01",
            "expiryDate.month" -> "01",
            "expiryDate.year" -> "2020"
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), fullName).right.get

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, passportDetailsControllerRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day" -> "01",
            "expiryDate.month" -> "01",
            "expiryDate.year" -> "2020"
          )

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), fullName).right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, passportDetailsControllerRoute)
          .withFormUrlEncodedBody(("value", ""))

      val result = route(application, request).value

      val boundForm = form.bind(Map("value" -> ""))

      val countryOption: CountryOptions = application.injector.instanceOf[CountryOptions]

      val view = application.injector.instanceOf[PassportDetailsView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOption.options(), fakeDraftId, index, fullName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, passportDetailsControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, passportDetailsControllerRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "Italy"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}

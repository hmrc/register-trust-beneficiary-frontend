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

package controllers.register.beneficiaries.charityortrust.trust.nonTaxable

import base.SpecBase
import config.annotations.TrustBeneficiary
import connectors.SubmissionDraftConnector
import forms.CountryFormProvider
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.register.beneficiaries.charityortrust.trust.NamePage
import pages.register.beneficiaries.charityortrust.trust.nonTaxable.CountryOfResidencePage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.beneficiaries.charityortrust.trust.nonTaxable.CountryOfResidenceView

import scala.concurrent.Future

class CountryOfResidenceControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new CountryFormProvider()
  val form: Form[String] = formProvider.withPrefix("trust.nonTaxable.countryOfResidence")
  val index: Int = 0
  val trustName = "Test"

  lazy val countryOfResidence: String = routes.CountryOfResidenceController.onPageLoad(index, draftId).url

  "CountryOfResidence Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidence)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountryOfResidenceView]

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, draftId, index, trustName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), trustName).success.value
        .set(CountryOfResidencePage(index), "Spain").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidence)

      val view = application.injector.instanceOf[CountryOfResidenceView]

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("Spain"), countryOptions, draftId, index, trustName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).success.value

      val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector),
          bind[Navigator].qualifiedWith(classOf[TrustBeneficiary]).toInstance(new FakeNavigator)
        ).build()

      when(mockSubmissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))

      val request =
        FakeRequest(POST, countryOfResidence)
          .withFormUrlEncodedBody(("value", "ES"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfResidence)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountryOfResidenceView]

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, draftId, index, trustName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, countryOfResidence)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, countryOfResidence)
          .withFormUrlEncodedBody(("value", "ES"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}


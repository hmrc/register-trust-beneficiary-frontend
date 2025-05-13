/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.register.beneficiaries.companyoremploymentrelated.company.mld5

import base.SpecBase
import config.annotations.CompanyBeneficiary
import controllers.actions.BeneficiaryNameRequest
import errors.ServerError
import forms.YesNoFormProvider
import models.requests.RegistrationDataRequest
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.companyoremploymentrelated.company.NamePage
import pages.register.beneficiaries.companyoremploymentrelated.company.mld5.CountryOfResidenceYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import views.html.register.beneficiaries.companyoremploymentrelated.company.mld5.CountryOfResidenceYesNoView
import views.html.{PageNotFoundView, TechnicalErrorView}

class CountryOfResidenceYesNoControllerSpec extends SpecBase {

  private val formProvider = new YesNoFormProvider()
  private val form: Form[Boolean] = formProvider.withPrefix("companyBeneficiary.5mld.countryOfResidenceYesNo")
  private val index: Int = 0
  private val trustName = "Test"

  private lazy val countryOfResidenceYesNo: String = routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url

  "CountryOfResidenceYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceYesNo)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountryOfResidenceYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId, index, trustName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), trustName).value
        .set(CountryOfResidenceYesNoPage(index), true).value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceYesNo)

      val view = application.injector.instanceOf[CountryOfResidenceYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), draftId, index, trustName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[CompanyBeneficiary]).toInstance(new FakeNavigator)
        ).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).value

      val application = applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[CompanyBeneficiary]).toInstance(new FakeNavigator)
        ).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trustName).value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountryOfResidenceYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, index, trustName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, countryOfResidenceYesNo)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to PageNotFoundView given no user answers are found for NamePage" in {
      val pageNotFoundView = app.injector.instanceOf[PageNotFoundView]

      val fakeRequest = FakeRequest(GET, countryOfResidenceYesNo)
      val registrationDataRequest =
        RegistrationDataRequest(
          fakeRequest,
          "internalId",
          "sessionId",
          userAnswers = emptyUserAnswers,
          AffinityGroup.Agent,
          Enrolments(Set.empty[Enrolment])
        )

      val controller = app.injector.instanceOf[CountryOfResidenceYesNoController]
      val beneficiaryNameRequest = BeneficiaryNameRequest(registrationDataRequest, "beneficiaryName")
      val index = 0

      val result = controller.handlePageLoad(index, "draftId")(beneficiaryNameRequest)

      status(result) mustEqual NOT_FOUND
      contentAsString(result) mustEqual pageNotFoundView()(fakeRequest, messages).toString()
    }

  }
}


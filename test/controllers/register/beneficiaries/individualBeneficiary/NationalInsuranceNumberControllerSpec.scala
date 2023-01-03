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
import config.annotations.IndividualBeneficiary
import forms.NationalInsuranceNumberFormProvider
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.individual.{NamePage, NationalInsuranceNumberPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.beneficiaries.individualBeneficiary.NationalInsuranceNumberView

class NationalInsuranceNumberControllerSpec extends SpecBase {

  val formProvider = new NationalInsuranceNumberFormProvider()
  val index: Int = 0
  val form: Form[String] = formProvider.withPrefix("individualBeneficiaryNationalInsuranceNumber", emptyUserAnswers, index)

  val name: FullName = FullName("first name", None, "Last name")

  lazy val individualBeneficiaryNationalInsuranceNumberRoute: String = routes.NationalInsuranceNumberController.onPageLoad(index, fakeDraftId).url

  "IndividualBeneficiaryNationalInsuranceNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index),
        name).right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryNationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NationalInsuranceNumberPage(index), "answer").right.get
        .set(NamePage(index),name).right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryNationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index),
        name).right.get

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, individualBeneficiaryNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "JP123456A"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors" when {
      "invalid data is submitted" in {

        val userAnswers = emptyUserAnswers.set(NamePage(index),
          name).right.get

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, individualBeneficiaryNationalInsuranceNumberRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[NationalInsuranceNumberView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, name, index)(request, messages).toString

        application.stop()
      }

      "duplicate nino is submitted" in {

        val nino = "JH123456C"

        val userAnswers = emptyUserAnswers
          .set(NamePage(index), name).right.get
          .set(NationalInsuranceNumberPage(index + 1), nino).right.get

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, individualBeneficiaryNationalInsuranceNumberRoute)
            .withFormUrlEncodedBody(("value", nino))

        val boundForm = form
          .bind(Map("value" -> nino))
          .withError("value", "individualBeneficiaryNationalInsuranceNumber.error.duplicate")

        val view = application.injector.instanceOf[NationalInsuranceNumberView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, name, index)(request, messages).toString

        application.stop()
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, individualBeneficiaryNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, individualBeneficiaryNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.NumberOfBeneficiariesFormProvider
import models.registration.pages.HowManyBeneficiaries
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.LargeBeneficiaryNumberOfBeneficiariesPage
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.beneficiaries.companyoremploymentrelated.employmentRelated.NumberOfBeneficiariesView

class NumberOfBeneficiariesControllerSpec extends SpecBase with MockitoSugar {

  private val index = 0
  private val form: Form[HowManyBeneficiaries] = new NumberOfBeneficiariesFormProvider()()
  private val numberOfBeneficiariesRoute: String = routes.NumberOfBeneficiariesController.onPageLoad(index, draftId).url
  private val numberOfBeneficiaries: HowManyBeneficiaries = HowManyBeneficiaries.Over201
  private val onwardRoute = Call("GET", "/foo")

  "NumberOfBeneficiaries Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, numberOfBeneficiariesRoute)

      val view = application.injector.instanceOf[NumberOfBeneficiariesView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, index, draftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = emptyUserAnswers.set(LargeBeneficiaryNumberOfBeneficiariesPage(index), numberOfBeneficiaries).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, numberOfBeneficiariesRoute)

      val view = application.injector.instanceOf[NumberOfBeneficiariesView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(numberOfBeneficiaries), index, draftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[EmploymentRelatedBeneficiary]).toInstance(new FakeNavigator(onwardRoute))
          ).build()

      val request =
        FakeRequest(POST, numberOfBeneficiariesRoute)
          .withFormUrlEncodedBody(("value", numberOfBeneficiaries.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, numberOfBeneficiariesRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NumberOfBeneficiariesView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, index, draftId)(request, messages).toString

       application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, numberOfBeneficiariesRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, numberOfBeneficiariesRoute)
          .withFormUrlEncodedBody(("value", numberOfBeneficiaries.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}

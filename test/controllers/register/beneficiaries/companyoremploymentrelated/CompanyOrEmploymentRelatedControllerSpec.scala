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

package controllers.register.beneficiaries.companyoremploymentrelated

import base.SpecBase
import forms.CompanyOrEmploymentRelatedBeneficiaryTypeFormProvider
import models.CompanyOrEmploymentRelatedToAdd
import org.scalatestplus.mockito.MockitoSugar
import pages.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedView

class CompanyOrEmploymentRelatedControllerSpec extends SpecBase with MockitoSugar {

  private val form: Form[CompanyOrEmploymentRelatedToAdd] = new CompanyOrEmploymentRelatedBeneficiaryTypeFormProvider()()
  private lazy val companyOrEmploymentRelatedRoute: String = routes.CompanyOrEmploymentRelatedController.onPageLoad(draftId).url
  private val validAnswer = CompanyOrEmploymentRelatedToAdd.Company

  "CompanyOrEmploymentRelatedController Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, companyOrEmploymentRelatedRoute)

      val view = application.injector.instanceOf[CompanyOrEmploymentRelatedView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId)(request, messages).toString

      application.stop()
    }

    "populate the view without the previous answer when the question has previously been answered" in {

      val answers = emptyUserAnswers.set(CompanyOrEmploymentRelatedPage, validAnswer).right.get

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, companyOrEmploymentRelatedRoute)

      val view = application.injector.instanceOf[CompanyOrEmploymentRelatedView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), draftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, companyOrEmploymentRelatedRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, companyOrEmploymentRelatedRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CompanyOrEmploymentRelatedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual view(boundForm, draftId)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, companyOrEmploymentRelatedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, companyOrEmploymentRelatedRoute)
          .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

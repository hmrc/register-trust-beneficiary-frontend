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
import errors.ServerError
import forms.RemoveIndexFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.companyoremploymentrelated.company.NamePage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{RemoveIndexView, TechnicalErrorView}

class RemoveEmploymentRelatedBeneficiaryControllerSpec extends SpecBase with ScalaCheckPropertyChecks {

  private val messagesPrefix = "removeEmploymentRelatedBeneficiaryYesNo"

  private val formProvider = new RemoveIndexFormProvider()
  private val form: Form[Boolean] = formProvider(messagesPrefix)

  private lazy val formRoute: Call = routes.RemoveEmploymentRelatedBeneficiaryController.onSubmit(0, fakeDraftId)

  private val index = 0

  "RemoveEmploymentRelatedBeneficiary Controller" when {

    "no name added" must {

      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemoveEmploymentRelatedBeneficiaryController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(messagesPrefix, form, index, fakeDraftId, "the employment related beneficiary", formRoute)(request, messages).toString

        application.stop()
      }

    }

    "name is provided" must {

      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers.set(NamePage(0), "Employment Related").value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemoveEmploymentRelatedBeneficiaryController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(messagesPrefix, form, index, fakeDraftId, "Employment Related", formRoute)(request, messages).toString

        application.stop()
      }

    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(0), "Employment Related").value

      forAll(arbitrary[Boolean]) {
        value =>
          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .build()

          val request =
            FakeRequest(POST, routes.RemoveEmploymentRelatedBeneficiaryController.onSubmit(index, fakeDraftId).url)
              .withFormUrlEncodedBody(("value", value.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

          application.stop()
      }

    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val userAnswers = emptyUserAnswers.set(NamePage(0), "Employment Related").value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
          .build()

      val request =
        FakeRequest(POST, routes.RemoveEmploymentRelatedBeneficiaryController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", true.toString))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(0), "Employment related").value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, routes.RemoveEmploymentRelatedBeneficiaryController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(messagesPrefix, boundForm, index, fakeDraftId, "Employment related", formRoute)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RemoveEmploymentRelatedBeneficiaryController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, routes.RemoveEmploymentRelatedBeneficiaryController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

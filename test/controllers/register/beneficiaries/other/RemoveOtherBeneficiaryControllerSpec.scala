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

package controllers.register.beneficiaries.other

import base.SpecBase
import forms.RemoveIndexFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.other.DescriptionPage
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.RemoveIndexView

class RemoveOtherBeneficiaryControllerSpec extends SpecBase with ScalaCheckPropertyChecks {

  val messagesPrefix = "removeOtherBeneficiaryYesNo"

  val formProvider = new RemoveIndexFormProvider()
  val form: Form[Boolean] = formProvider(messagesPrefix)

  lazy val formRoute: Call = routes.RemoveOtherBeneficiaryController.onSubmit(0, fakeDraftId)

  val index = 0

  "RemoveOtherBeneficiary Controller" when {

    "no name added" must {

      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(messagesPrefix, form, index, fakeDraftId, "the other beneficiary", formRoute)(request, messages).toString

        application.stop()
      }

    }

    "name is provided" must {

      "return OK and the correct view for a GET" in {

        val userAnswers = emptyUserAnswers.set(DescriptionPage(0), "other beneficiary").right.get

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(index, fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(messagesPrefix, form, index, fakeDraftId, "other beneficiary", formRoute)(request, messages).toString

        application.stop()
      }

    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(DescriptionPage(0), "other beneficiary").right.get

      forAll(arbitrary[Boolean]) {
        value =>
        val application =
          applicationBuilder(userAnswers = Some(userAnswers))
            .build()

        val request =
          FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index, fakeDraftId).url)
            .withFormUrlEncodedBody(("value", value.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(DescriptionPage(0), "other beneficiary").right.get

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(messagesPrefix, boundForm, index, fakeDraftId, "other beneficiary", formRoute)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.RemoveOtherBeneficiaryController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, routes.RemoveOtherBeneficiaryController.onSubmit(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

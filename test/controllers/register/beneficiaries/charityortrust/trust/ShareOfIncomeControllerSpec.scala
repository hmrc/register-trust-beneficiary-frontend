/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.register.beneficiaries.charityortrust.trust

import base.SpecBase
import config.annotations.TrustBeneficiary
import errors.ServerError
import forms.IncomePercentageFormProvider
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.charityortrust.trust.{NamePage, ShareOfIncomePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.trust.ShareOfIncomeView

class ShareOfIncomeControllerSpec extends SpecBase {

  private val formProvider    = new IncomePercentageFormProvider()
  private val form: Form[Int] = formProvider.withPrefix("trustBeneficiaryShareOfIncome")
  private val name            = "Name"
  private val index: Int      = 0

  private val userAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage(index), name)
    .value

  private lazy val shareOfIncomeRoute: String = routes.ShareOfIncomeController.onPageLoad(index, fakeDraftId).url

  "ShareOfIncome Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, shareOfIncomeRoute)

      val view = application.injector.instanceOf[ShareOfIncomeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = userAnswers
        .set(ShareOfIncomePage(index), 5)
        .value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, shareOfIncomeRoute)

      val view = application.injector.instanceOf[ShareOfIncomeView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(5), fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[TrustBeneficiary]).toInstance(new FakeNavigator)
          )
          .build()

      val request =
        FakeRequest(POST, shareOfIncomeRoute)
          .withFormUrlEncodedBody(("value", "5"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[TrustBeneficiary]).toInstance(new FakeNavigator)
          )
          .build()

      val request =
        FakeRequest(POST, shareOfIncomeRoute)
          .withFormUrlEncodedBody(("value", "5"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = app.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, shareOfIncomeRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ShareOfIncomeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, shareOfIncomeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, shareOfIncomeRoute)
          .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}

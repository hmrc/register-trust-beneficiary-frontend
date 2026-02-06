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

package controllers.register.beneficiaries.individualBeneficiary

import base.SpecBase
import config.annotations.IndividualBeneficiary
import errors.{ServerError, TrustErrors}
import forms.RoleInCompanyFormProvider
import models.UserAnswers
import models.core.pages.FullName
import models.registration.pages.RoleInCompany
import models.registration.pages.RoleInCompany.Director
import navigation.{FakeNavigator, Navigator}
import pages.register.beneficiaries.individual.{NamePage, RoleInCompanyPage}
import play.api.Application
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.individualBeneficiary.RoleInCompanyView

class RoleInCompanyControllerSpec extends SpecBase {

  private val formProvider              = new RoleInCompanyFormProvider()
  private val form: Form[RoleInCompany] = formProvider()
  private val name: FullName            = FullName("FirstName", None, "LastName")
  private val index                     = 0

  private val userAnswers: UserAnswers = emptyUserAnswers.set(NamePage(index), name).value

  private def application(repositorySetResult: Either[TrustErrors, Boolean] = Right(true)): Application =
    applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = repositorySetResult)
      .overrides(
        bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
      )
      .build()

  private lazy val roleInCompanyControllerRoute: String = routes.RoleInCompanyController.onPageLoad(index, draftId).url

  "AddressYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val request = FakeRequest(GET, roleInCompanyControllerRoute)

      val result = route(application(), request).value

      val view = application().injector.instanceOf[RoleInCompanyView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId, name, index)(request, messages).toString

      application().stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name)
        .value
        .set(RoleInCompanyPage(index), Director)
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, roleInCompanyControllerRoute)

      val view = application.injector.instanceOf[RoleInCompanyView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(Director), draftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val request =
        FakeRequest(POST, roleInCompanyControllerRoute)
          .withFormUrlEncodedBody(("value", Director.toString))

      val result = route(application(), request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application().stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val request =
        FakeRequest(POST, roleInCompanyControllerRoute)
          .withFormUrlEncodedBody(("value", Director.toString))

      val result = route(application(Left(ServerError())), request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application(Left(ServerError())).injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application(Left(ServerError())).stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val request =
        FakeRequest(POST, roleInCompanyControllerRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application().injector.instanceOf[RoleInCompanyView]

      val result = route(application(), request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, name, index)(request, messages).toString

      application().stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, roleInCompanyControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, roleInCompanyControllerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}

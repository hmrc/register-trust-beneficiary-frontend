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
import cats.data.EitherT
import config.annotations.IndividualBeneficiary
import errors.{ServerError, TrustErrors}
import forms.NameFormProvider
import models.core.pages.FullName
import models.{ReadOnlyUserAnswers, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.register.beneficiaries.individual.NamePage
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.individualBeneficiary.NameView

import scala.concurrent.Future

class NameControllerSpec extends SpecBase {

  private val formProvider = new NameFormProvider()
  private val form: Form[FullName] = formProvider.withPrefix("individualBeneficiaryName")
  private val name: FullName = FullName("first name", Some("middle name"), "last name")
  private val index: Int = 0

  private lazy val individualBeneficiaryNameRoute: String = routes.NameController.onPageLoad(index, fakeDraftId).url

  private val userAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage(index), name).value

  "IndividualBeneficiaryName Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryNameRoute)

      val view = application.injector.instanceOf[NameView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, individualBeneficiaryNameRoute)

      val view = application.injector.instanceOf[NameView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(name), fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {
      "using main answers" in {

        when(mockRegistrationsRepository.getSettlorsAnswers(any())(any()))
          .thenReturn(EitherT[Future, TrustErrors, Option[ReadOnlyUserAnswers]](Future.successful(Right(Some(ReadOnlyUserAnswers(Json.obj()))))))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

        val request =
          FakeRequest(POST, individualBeneficiaryNameRoute)
            .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }
      "using section answers" in {

        when(mockRegistrationsRepository.getSettlorsAnswers(any())(any()))
          .thenReturn(EitherT[Future, TrustErrors, Option[ReadOnlyUserAnswers]](Future.successful(Right(None))))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

        val request =
          FakeRequest(POST, individualBeneficiaryNameRoute)
            .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }
    }

    "return an Internal Server Error when setting the user answers goes wrong" when {
      "using main answers" in {

        when(mockRegistrationsRepository.getSettlorsAnswers(any())(any()))
          .thenReturn(EitherT[Future, TrustErrors, Option[ReadOnlyUserAnswers]](Future.successful(Right(Some(ReadOnlyUserAnswers(Json.obj()))))))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

        val request =
          FakeRequest(POST, individualBeneficiaryNameRoute)
            .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        val errorPage = application.injector.instanceOf[TechnicalErrorView]

        contentType(result) mustBe Some("text/html")
        contentAsString(result) mustEqual errorPage()(request, messages).toString

        application.stop()
      }
      "using section answers" in {

        when(mockRegistrationsRepository.getSettlorsAnswers(any())(any()))
          .thenReturn(EitherT[Future, TrustErrors, Option[ReadOnlyUserAnswers]](Future.successful(Right(None))))

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

        val request =
          FakeRequest(POST, individualBeneficiaryNameRoute)
            .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        val errorPage = application.injector.instanceOf[TechnicalErrorView]

        contentType(result) mustBe Some("text/html")
        contentAsString(result) mustEqual errorPage()(request, messages).toString

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, individualBeneficiaryNameRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[NameView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, individualBeneficiaryNameRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, individualBeneficiaryNameRoute)
          .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

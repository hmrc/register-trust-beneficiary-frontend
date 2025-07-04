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
import forms.NationalInsuranceNumberFormProvider
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.register.beneficiaries.individual.{NamePage, NationalInsuranceNumberPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TechnicalErrorView
import services.DraftRegistrationService
import views.html.register.beneficiaries.individualBeneficiary.NationalInsuranceNumberView

import scala.concurrent.Future

class NationalInsuranceNumberControllerSpec extends SpecBase {

  private val formProvider = new NationalInsuranceNumberFormProvider()
  private val index: Int = 0
  private val existingSettlorNinos = Seq("")
  private val form: Form[String] = formProvider.withPrefix("individualBeneficiaryNationalInsuranceNumber", emptyUserAnswers, index, existingSettlorNinos)
  private val name: FullName = FullName("first name", None, "Last name")

  private lazy val individualBeneficiaryNationalInsuranceNumberRoute: String = routes.NationalInsuranceNumberController.onPageLoad(index, fakeDraftId).url

  "IndividualBeneficiaryNationalInsuranceNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Right(""))))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request = FakeRequest(GET, individualBeneficiaryNationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "return internal server error when there is a problem retrieving settlor ninos for GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Left(ServerError()))))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request = FakeRequest(GET, individualBeneficiaryNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NationalInsuranceNumberPage(index), "answer").value
        .set(NamePage(index),name).value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Right(""))))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request = FakeRequest(GET, individualBeneficiaryNationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), fakeDraftId, name, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Right(""))))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[DraftRegistrationService].toInstance(mockDraftRegistrationService),
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
    "return internal server error when there is a problem retrieving settlor ninos for POST" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Left(ServerError()))))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[DraftRegistrationService].toInstance(mockDraftRegistrationService),
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, individualBeneficiaryNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "JP123456A"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Right(""))))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), mockSetResult = Left(ServerError()))
          .overrides(
            bind[DraftRegistrationService].toInstance(mockDraftRegistrationService),
            bind[Navigator].qualifiedWith(classOf[IndividualBeneficiary]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, individualBeneficiaryNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "JP123456A"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors" when {
      "invalid data is submitted" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage(index), name).value

        val mockDraftRegistrationService = mock[DraftRegistrationService]

        when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Right(""))))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

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
          .set(NamePage(index), name).value
          .set(NationalInsuranceNumberPage(index + 1), nino).value

        val mockDraftRegistrationService = mock[DraftRegistrationService]

        when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(EitherT[Future, TrustErrors, String](Future.successful(Right(""))))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

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

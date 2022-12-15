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

package controllers.register.beneficiaries.charityortrust.trust

import base.SpecBase
import errors.ServerError
import models.Status.Completed
import models.UserAnswers
import models.registration.pages.CharityOrTrust.Trust
import models.registration.pages.WhatTypeOfBeneficiary.CharityOrTrust
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.entitystatus.TrustBeneficiaryStatus
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.charityortrust.trust._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.print.TrustBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.charityortrust.trust.AnswersView

class AnswersControllerSpec extends SpecBase {

  private val index = 0

  private lazy val answersRoute = routes.AnswersController.onPageLoad(index, fakeDraftId).url

  override def emptyUserAnswers: UserAnswers = super.emptyUserAnswers
    .set(WhatTypeOfBeneficiaryPage, CharityOrTrust).right.get
    .set(CharityOrTrustPage, Trust).right.get
    .set(NamePage(index), "Trust Name").right.get

  "TrustBeneficiaryAnswers Controller" must {

    "return OK and the correct view for a GET" in {

      val mockPrintHelper: TrustBeneficiaryPrintHelper = mock[TrustBeneficiaryPrintHelper]

      val fakeAnswerSection: AnswerSection = AnswerSection()

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[TrustBeneficiaryPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, answersRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(fakeAnswerSection), index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "amend user answers and redirect" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val request = FakeRequest(POST, answersRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockRegistrationsRepository).set(uaCaptor.capture)(any(), any())
      uaCaptor.getValue.get(TrustBeneficiaryStatus(index)).get mustBe Completed

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError()))
        .build()

      val request = FakeRequest(POST, answersRoute)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = app.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }
  }
}

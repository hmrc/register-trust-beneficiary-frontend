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
import errors.ServerError
import models.Status.Completed
import models.UserAnswers
import models.registration.pages.WhatTypeOfBeneficiary.Other
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import pages.entitystatus.OtherBeneficiaryStatus
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import pages.register.beneficiaries.other.DescriptionPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.print.OtherBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.other.CheckDetailsView

class CheckDetailsControllerSpec extends SpecBase {

  private val index = 0

  private lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  override def emptyUserAnswers: UserAnswers = super.emptyUserAnswers
    .set(WhatTypeOfBeneficiaryPage, Other).right.get
    .set(DescriptionPage(index), "Description").right.get

  "CheckDetailsController" must {

    "return OK and the correct view for a GET" in {

      val mockPrintHelper: OtherBeneficiaryPrintHelper = mock[OtherBeneficiaryPrintHelper]

      val fakeAnswerSection: AnswerSection = AnswerSection()

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[OtherBeneficiaryPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(fakeAnswerSection), index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "amend user answers and redirect" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val request = FakeRequest(POST, checkDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockRegistrationsRepository).set(uaCaptor.capture)(any(), any())
      uaCaptor.getValue.get(OtherBeneficiaryStatus(index)).get mustBe Completed

      application.stop()
    }

    "return an Internal Server Error when setting the user answers goes wrong" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError()))
        .build()

      val request = FakeRequest(POST, checkDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val errorPage = application.injector.instanceOf[TechnicalErrorView]

      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }
  }
}

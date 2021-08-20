/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.beneficiaries.charityortrust.charity

import base.SpecBase
import models.Status.Completed
import models.UserAnswers
import models.registration.pages.CharityOrTrust.Charity
import models.registration.pages.WhatTypeOfBeneficiary.CharityOrTrust
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import pages.entitystatus.CharityBeneficiaryStatus
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import pages.register.beneficiaries.charityortrust._
import pages.register.beneficiaries.charityortrust.charity._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.print.CharityBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.register.beneficiaries.charityortrust.charity.CharityAnswersView

class CharityAnswersControllerSpec extends SpecBase {

  private val index = 0

  private lazy val charityAnswersRoute = routes.CharityAnswersController.onPageLoad(index, fakeDraftId).url

  override def emptyUserAnswers: UserAnswers = super.emptyUserAnswers
    .set(WhatTypeOfBeneficiaryPage, CharityOrTrust).success.value
    .set(CharityOrTrustPage, Charity).success.value
    .set(CharityNamePage(index),"Test").success.value

  "CharityAnswers Controller" must {

    "return OK and the correct view for a GET" in {

      val mockPrintHelper: CharityBeneficiaryPrintHelper = mock[CharityBeneficiaryPrintHelper]

      val fakeAnswerSection: AnswerSection = AnswerSection()

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[CharityBeneficiaryPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, charityAnswersRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CharityAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(fakeAnswerSection), index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "amend user answers and redirect" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .build()

      val request = FakeRequest(POST, charityAnswersRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

      val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
      uaCaptor.getValue.get(CharityBeneficiaryStatus(index)).get mustBe Completed

      application.stop()
    }
  }
}

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

package controllers.register.beneficiaries.individualBeneficiary

import base.SpecBase
import models.core.pages.{FullName, UKAddress}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.individual.mld5._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.Constants._
import utils.print.IndividualBeneficiaryPrintHelper
import viewmodels.AnswerSection
import views.html.register.beneficiaries.individualBeneficiary.AnswersView

import java.time.{LocalDate, ZoneOffset}

class AnswersControllerSpec extends SpecBase {

  val index = 0

  "IndividualBeneficiaryAnswers Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("first name", None, "last name")).success.value
        .set(DateOfBirthYesNoPage(index),true).success.value
        .set(DateOfBirthPage(index),LocalDate.now(ZoneOffset.UTC)).success.value
        .set(IncomeYesNoPage(index),true).success.value
        .set(IncomePage(index),100).success.value
        .set(CountryOfNationalityYesNoPage(index), true).success.value
        .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value
        .set(CountryOfNationalityPage(index), ES).success.value
        .set(NationalInsuranceYesNoPage(index),true).success.value
        .set(NationalInsuranceNumberPage(index),"AB123456C").success.value
        .set(CountryOfResidenceYesNoPage(index), true).success.value
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(CountryOfResidencePage(index), ES).success.value
        .set(AddressYesNoPage(index),true).success.value
        .set(AddressUKYesNoPage(index),true).success.value
        .set(AddressUKPage(index),UKAddress("Line1", "Line2", None, None, "NE62RT")).success.value
        .set(MentalCapacityYesNoPage(index), true).success.value
        .set(VulnerableYesNoPage(index),true).success.value

      val mockPrintHelper: IndividualBeneficiaryPrintHelper = mock[IndividualBeneficiaryPrintHelper]

      val fakeAnswerSection: AnswerSection = AnswerSection()

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[IndividualBeneficiaryPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, routes.AnswersController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeAnswerSection, index, fakeDraftId)(request, messages).toString

      application.stop()
    }
  }
}

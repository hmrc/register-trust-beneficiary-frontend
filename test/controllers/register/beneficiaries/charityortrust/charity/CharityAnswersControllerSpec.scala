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
import models.core.pages.UKAddress
import models.registration.pages.CharityOrTrust.Charity
import pages.register.beneficiaries.charityortrust._
import pages.register.beneficiaries.charityortrust.charity.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import pages.register.beneficiaries.charityortrust.charity.{AddressInTheUkYesNoPage, AddressYesNoPage, AmountDiscretionYesNoPage, CharityAddressUKPage, CharityNamePage, HowMuchIncomePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.answers.CharityBeneficiaryAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.beneficiaries.charityortrust.charity.CharityAnswersView

class CharityAnswersControllerSpec extends SpecBase {

  val index = 0

  "CharityAnswers Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers =
        emptyUserAnswers
          .set(CharityOrTrustPage, Charity).success.value
          .set(CharityNamePage(index),"Test").success.value
          .set(AmountDiscretionYesNoPage(index), false).success.value
          .set(HowMuchIncomePage(index),60).success.value
          .set(CountryOfResidenceYesNoPage(index), true).success.value
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
          .set(CountryOfResidencePage(index), "ES").success.value
          .set(AddressYesNoPage(index),true).success.value
          .set(AddressInTheUkYesNoPage(index),true).success.value
          .set(CharityAddressUKPage(index),UKAddress("Test 1","Test 2", None, None, "AB11AB")).success.value

      val countryOptions = injector.instanceOf[CountryOptions]
      val checkYourAnswersHelper = new CharityBeneficiaryAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.charityName(index).value,
            checkYourAnswersHelper.amountDiscretionYesNo(index).value,
            checkYourAnswersHelper.howMuchIncome(index).value,
            checkYourAnswersHelper.countryOfResidenceYesNo(index).value,
            checkYourAnswersHelper.countryOfResidenceInUkYesNo(index).value,
            checkYourAnswersHelper.countryOfResidence(index).value,
            checkYourAnswersHelper.addressYesNo(index).value,
            checkYourAnswersHelper.addressInTheUkYesNo(index).value,
            checkYourAnswersHelper.charityAddressUK(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.CharityAnswersController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CharityAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections)(request, messages).toString

      application.stop()
    }
  }
}

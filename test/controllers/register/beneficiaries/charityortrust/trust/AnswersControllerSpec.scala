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

package controllers.register.beneficiaries.charityortrust.trust

import base.SpecBase
import models.core.pages.UKAddress
import pages.register.beneficiaries.charityortrust.trust._
import pages.register.beneficiaries.charityortrust.trust.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.Constants._
import utils.answers.{CheckAnswersFormatters, TrustBeneficiaryAnswersHelper}
import viewmodels.AnswerSection
import views.html.register.beneficiaries.charityortrust.trust.AnswersView

class AnswersControllerSpec extends SpecBase {

  val index = 0

  "TrustBeneficiaryAnswers Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), "Trust Name").success.value
        .set(DiscretionYesNoPage(index),true).success.value
        .set(ShareOfIncomePage(index),100).success.value
        .set(CountryOfResidenceYesNoPage(index), true).success.value
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(CountryOfResidencePage(index), ES).success.value
        .set(AddressYesNoPage(index),true).success.value
        .set(AddressUKYesNoPage(index),true).success.value
        .set(AddressUKPage(index),UKAddress("Line1", "Line2", None, None, "NE62RT")).success.value

      val checkAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]
      val checkYourAnswersHelper = new TrustBeneficiaryAnswersHelper(checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit = true)

      val expectedSections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.trustBeneficiaryName(index).value,
            checkYourAnswersHelper.trustBeneficiaryDiscretionYesNo(index).value,
            checkYourAnswersHelper.trustBeneficiaryShareOfIncome(index).value,
            checkYourAnswersHelper.trustBeneficiaryCountryOfResidenceYesNo(index).value,
            checkYourAnswersHelper.trustBeneficiaryCountryOfResidenceInTheUkYesNo(index).value,
            checkYourAnswersHelper.trustBeneficiaryCountryOfResidence(index).value,
            checkYourAnswersHelper.trustBeneficiaryAddressYesNo(index).value,
            checkYourAnswersHelper.trustBeneficiaryAddressUKYesNo(index).value,
            checkYourAnswersHelper.trustBeneficiaryAddressUK(index).value
          )
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.AnswersController.onPageLoad(index, fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(index, fakeDraftId, expectedSections)(request, messages).toString

      application.stop()
    }
  }
}

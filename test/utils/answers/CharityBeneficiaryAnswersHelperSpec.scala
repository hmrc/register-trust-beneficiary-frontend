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

package utils.answers

import base.SpecBase
import controllers.register.beneficiaries.charityortrust.charity.routes._
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.beneficiaries.charityortrust.charity._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class CharityBeneficiaryAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: String = "Name"
  private val amount: Int = 50
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val country: String = "FR"
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), country)
  private val canEdit: Boolean = true

  private val helper: CharityBeneficiaryAnswersHelper = injector.instanceOf[CharityBeneficiaryAnswersHelper]

  "Charity beneficiary answers helper" must {

    "return None for empty user answers" in {

      val result = helper.beneficiaries(emptyUserAnswers)

      result mustBe None
    }

    "return a charity beneficiary answer section" in {

      val userAnswers = emptyUserAnswers
        .set(CharityNamePage(index), name).success.value
        .set(AmountDiscretionYesNoPage(index), false).success.value
        .set(HowMuchIncomePage(index), amount).success.value
        .set(CountryOfResidenceYesNoPage(index), true).success.value
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(CountryOfResidencePage(index), country).success.value
        .set(AddressYesNoPage(index), true).success.value
        .set(AddressInTheUkYesNoPage(index), true).success.value
        .set(CharityAddressUKPage(index), ukAddress).success.value
        .set(CharityInternationalAddressPage(index), nonUkAddress).success.value

      val result = helper.beneficiaries(userAnswers).get

      result mustBe
        Seq(
          AnswerSection(
            headingKey = Some("answerPage.section.charityBeneficiary.subheading"),
            rows = Seq(
              AnswerRow("charity.name.checkYourAnswersLabel", Html(name), Some(CharityNameController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.discretionYesNo.checkYourAnswersLabel", Html("No"), Some(AmountDiscretionYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.shareOfIncome.checkYourAnswersLabel", Html("50%"), Some(HowMuchIncomeController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.countryOfResidenceYesNo.checkYourAnswersLabel", Html("Yes"), Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", Html("No"), Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.countryOfResidence.checkYourAnswersLabel", Html("France"), Some(CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.addressInTheUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), Some(CharityAddressUKController.onPageLoad(index, fakeDraftId).url), name, canEdit),
              AnswerRow("charity.internationalAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Line 3<br />France"), Some(CharityInternationalAddressController.onPageLoad(index, fakeDraftId).url), name, canEdit)
            ),
            headingArgs = Seq(index + 1)
          )
        )
    }
  }
}

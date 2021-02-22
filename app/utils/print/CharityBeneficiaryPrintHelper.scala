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

package utils.print

import com.google.inject.Inject
import controllers.register.beneficiaries.charityortrust.charity.mld5.routes._
import controllers.register.beneficiaries.charityortrust.charity.routes._
import models.UserAnswers
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.charity.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class CharityBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) extends PrintHelper {

  override val beneficiaryType: String = "charityBeneficiary"

  override def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                      (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.stringQuestion(CharityNamePage(index), "charity.name", CharityNameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AmountDiscretionYesNoPage(index), "charity.discretionYesNo", AmountDiscretionYesNoController.onPageLoad(index, draftId).url),
      bound.percentageQuestion(HowMuchIncomePage(index), "charity.shareOfIncome", HowMuchIncomeController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "charity.5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "charity.5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage(index), CountryOfResidencePage(index), "charity.5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "charity.addressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressInTheUkYesNoPage(index), "charity.addressInTheUkYesNo", AddressInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(CharityAddressUKPage(index), "charity.ukAddress", CharityAddressUKController.onPageLoad(index, draftId).url),
      bound.addressQuestion(CharityInternationalAddressPage(index), "charity.internationalAddress", CharityInternationalAddressController.onPageLoad(index, draftId).url)
    ).flatten

  }
}

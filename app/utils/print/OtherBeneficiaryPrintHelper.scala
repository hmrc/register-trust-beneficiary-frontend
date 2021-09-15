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
import controllers.register.beneficiaries.other.routes._
import models.UserAnswers
import pages.register.beneficiaries.other._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class OtherBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) extends PrintHelper {

  override val beneficiaryType: String = "otherBeneficiary"

  override def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                      (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.stringQuestion(DescriptionPage(index), "otherBeneficiary.description", DescriptionController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage(index), "otherBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(index, draftId).url),
      bound.percentageQuestion(ShareOfIncomePage(index), "otherBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "otherBeneficiary.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(UKResidentYesNoPage(index), "otherBeneficiary.ukResidentYesNo", UKResidentYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(UKResidentYesNoPage(index), CountryOfResidencePage(index), "otherBeneficiary.countryOfResidence", CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "otherBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUKYesNoPage(index), "otherBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressUKPage(index), "otherBeneficiary.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressInternationalPage(index), "otherBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(index, draftId).url)
    ).flatten

  }
}

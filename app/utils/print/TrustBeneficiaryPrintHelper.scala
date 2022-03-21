/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.register.beneficiaries.charityortrust.trust.mld5.routes._
import controllers.register.beneficiaries.charityortrust.trust.routes._
import models.UserAnswers
import pages.register.beneficiaries.charityortrust.trust._
import pages.register.beneficiaries.charityortrust.trust.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class TrustBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) extends PrintHelper {

  override val beneficiaryType: String = "trustBeneficiary"

  override def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                      (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.stringQuestion(NamePage(index), "trustBeneficiaryName", NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(DiscretionYesNoPage(index), "trustBeneficiaryDiscretionYesNo", DiscretionYesNoController.onPageLoad(index, draftId).url),
      bound.percentageQuestion(ShareOfIncomePage(index), "trustBeneficiaryShareOfIncome", ShareOfIncomeController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "trust.5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "trust.5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage(index), CountryOfResidencePage(index), "trust.5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "trustBeneficiaryAddressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUKYesNoPage(index), "trustBeneficiaryAddressUKYesNo", AddressUKYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressUKPage(index), "site.address.uk", AddressUKController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressInternationalPage(index), "site.address.international", AddressInternationalController.onPageLoad(index, draftId).url)
    ).flatten

  }
}

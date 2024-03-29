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

package utils.print

import com.google.inject.Inject
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.routes._
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.routes._
import models.UserAnswers
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.i18n.Messages
import viewmodels.AnswerRow

class EmploymentRelatedBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) extends PrintHelper {

  override val beneficiaryType: String = "largeBeneficiary"

  override def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                      (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.stringQuestion(LargeBeneficiaryNamePage(index), "employmentRelatedBeneficiary.name", NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "employmentRelatedBeneficiary.5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "employmentRelatedBeneficiary.5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage(index), CountryOfResidencePage(index), "employmentRelatedBeneficiary.5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(LargeBeneficiaryAddressYesNoPage(index), "employmentRelatedBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(LargeBeneficiaryAddressUKYesNoPage(index), "employmentRelatedBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(LargeBeneficiaryAddressPage(index), "employmentRelatedBeneficiary.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(LargeBeneficiaryAddressInternationalPage(index), "employmentRelatedBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(index, draftId).url),
      bound.descriptionQuestion(LargeBeneficiaryDescriptionPage(index), "employmentRelatedBeneficiary.description", DescriptionController.onPageLoad(index, draftId).url),
      bound.numberOfBeneficiariesQuestion(LargeBeneficiaryNumberOfBeneficiariesPage(index), "employmentRelatedBeneficiary.numberOfBeneficiaries", NumberOfBeneficiariesController.onPageLoad(index, draftId).url)
    ).flatten
  }
}

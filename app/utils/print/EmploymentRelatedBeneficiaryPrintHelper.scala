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
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.{routes => mld5Rts}
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => rts}
import models.UserAnswers
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class EmploymentRelatedBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def printSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit messages: Messages): AnswerSection = {
    AnswerSection(
      Some(Messages("answerPage.section.largeBeneficiary.subheading", index + 1)),
      answers(userAnswers, name, index, draftId)
    )
  }

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit messages: Messages): AnswerSection = {
    AnswerSection(
      None,
      answers(userAnswers, name, index, draftId)
    )
  }

  def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
             (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.stringQuestion(LargeBeneficiaryNamePage(index), "employmentRelatedBeneficiary.name", rts.NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "employmentRelatedBeneficiary.5mld.countryOfResidenceYesNo", mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "employmentRelatedBeneficiary.5mld.countryOfResidenceInTheUkYesNo", mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage(index), CountryOfResidencePage(index), "employmentRelatedBeneficiary.5mld.countryOfResidence", mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(LargeBeneficiaryAddressYesNoPage(index), "employmentRelatedBeneficiary.addressYesNo", rts.AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(LargeBeneficiaryAddressUKYesNoPage(index), "employmentRelatedBeneficiary.addressUkYesNo", rts.AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(LargeBeneficiaryAddressPage(index), "employmentRelatedBeneficiary.ukAddress", rts.UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(LargeBeneficiaryAddressInternationalPage(index), "employmentRelatedBeneficiary.nonUkAddress", rts.NonUkAddressController.onPageLoad(index, draftId).url),
      bound.descriptionQuestion(LargeBeneficiaryDescriptionPage(index), "employmentRelatedBeneficiary.description", rts.DescriptionController.onPageLoad(index, draftId).url),
      bound.numberOfBeneficiariesQuestion(LargeBeneficiaryNumberOfBeneficiariesPage(index), "employmentRelatedBeneficiary.numberOfBeneficiaries", rts.NumberOfBeneficiariesController.onPageLoad(index, draftId).url)
    ).flatten
  }
}

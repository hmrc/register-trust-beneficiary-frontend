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

import com.google.inject.Inject
import controllers.register.beneficiaries.other.routes._
import models.UserAnswers
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.other.mld5.{BeneficiariesAddressInUKYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.i18n.Messages
import sections.beneficiaries.OtherBeneficiaries
import utils.countryOptions.CountryOptions
import utils.print.AnswerRowConverter
import viewmodels.{AnswerRow, AnswerSection}


class OtherBeneficiaryAnswersHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                              countryOptions: CountryOptions
                                 ) {

  def otherBeneficiaries(userAnswers: UserAnswers)(implicit messages: Messages): Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(OtherBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (beneficiaryViewModel, index) =>
        val description = beneficiaryViewModel.description.getOrElse("")
        AnswerSection(
          Some(Messages("answerPage.section.otherBeneficiary.subheading", index + 1)),
          answers(userAnswers, description, index, userAnswers.draftId)
        )
    }
  }

  def checkDetailsSection(userAnswers: UserAnswers, description: String, index: Int, draftId: String)(implicit messages: Messages): AnswerSection = {
    AnswerSection(
      None,
      answers(userAnswers, description, index, draftId)
    )
  }

  def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
             (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    Seq(
      bound.stringQuestion(DescriptionPage(index), "otherBeneficiary.description", DescriptionController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(BeneficiariesAddressInUKYesNoPage(index), "otherBeneficiary.beneficiaryAddressYesNo", controllers.register.beneficiaries.other.mld5.routes.BeneficiariesAddressInUKYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "otherBeneficiary.countryOfResidenceYesNo", controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(BeneficiariesAddressInUKYesNoPage(index), CountryOfResidencePage(index), "otherBeneficiary.countryOfResidence",controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IncomeDiscretionYesNoPage(index), "otherBeneficiary.discretionYesNo", DiscretionYesNoController.onPageLoad(index, draftId).url),
      bound.percentageQuestion(ShareOfIncomePage(index), "otherBeneficiary.shareOfIncome", ShareOfIncomeController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "otherBeneficiary.addressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUKYesNoPage(index), "otherBeneficiary.addressUkYesNo", AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressUKPage(index), "otherBeneficiary.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressInternationalPage(index), "otherBeneficiary.nonUkAddress", NonUkAddressController.onPageLoad(index, draftId).url)
    ).flatten
  }
}

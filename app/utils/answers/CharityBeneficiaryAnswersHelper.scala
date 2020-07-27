/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.register.beneficiaries.charityortrust.charity.routes._
import javax.inject.Inject
import models.UserAnswers
import pages.register.beneficiaries.charityortrust.charity._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import sections.beneficiaries.{CharityBeneficiaries, IndividualBeneficiaries}
import utils.answers.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CharityBeneficiaryAnswersHelper @Inject()(countryOptions: CountryOptions)
                                               (userAnswers: UserAnswers,
                                              draftId: String,
                                              canEdit: Boolean)
                                               (implicit messages: Messages) {

  def charityBeneficiaries: Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(CharityBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = charityBeneficiaryRows(index)

        AnswerSection(
          Some(Messages("answerPage.section.charityBeneficiary.subheading", index + 1)),
          questions,
          None
        )
    }
  }

  def charityBeneficiaryRows(index: Int): Seq[AnswerRow] = {
    Seq(
      charityName(index),
      amountDiscretionYesNo(index),
      howMuchIncome(index),
      addressYesNo(index),
      addressInTheUkYesNo(index),
      charityAddressUK(index),
      charityInternationalAddress(index)
    ).flatten
  }

  def addressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.addressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(AddressYesNoController.onPageLoad(index, draftId).url),
        charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def amountDiscretionYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AmountDiscretionYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.discretionYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(AmountDiscretionYesNoController.onPageLoad(index, draftId).url),
        charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def charityName(index: Int): Option[AnswerRow] = userAnswers.get(CharityNamePage(index)) map {
    x =>
      AnswerRow(
        "charity.name.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(CharityNameController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def howMuchIncome(index: Int): Option[AnswerRow] = userAnswers.get(HowMuchIncomePage(index)) map {
    x =>
      AnswerRow(
        "charity.shareOfIncome.checkYourAnswersLabel",
        percentage(x.toString),
        Some(HowMuchIncomeController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def charityInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(CharityInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "charity.internationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(CharityInternationalAddressController.onPageLoad(index, draftId).url),
        charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def charityAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(CharityAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "charity.ukAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(CharityAddressUKController.onPageLoad(index, draftId).url),
        charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def addressInTheUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressInTheUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.addressInTheUkYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(AddressInTheUkYesNoController.onPageLoad(index, draftId).url),
        charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

}

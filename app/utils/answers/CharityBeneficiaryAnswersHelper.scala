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

import controllers.register.beneficiaries.charityortrust.charity.mld5.{routes => ntRts}
import controllers.register.beneficiaries.charityortrust.charity.{routes => rts}
import models.UserAnswers
import pages.register.beneficiaries.charityortrust.charity._
import pages.register.beneficiaries.charityortrust.charity.mld5._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import sections.beneficiaries.CharityBeneficiaries
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class CharityBeneficiaryAnswersHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters)
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
      countryOfResidenceYesNo(index),
      countryOfResidenceInUkYesNo(index),
      countryOfResidence(index),
      addressYesNo(index),
      addressInTheUkYesNo(index),
      charityAddressUK(index),
      charityInternationalAddress(index)
    ).flatten
  }

  def countryOfResidenceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(CountryOfResidenceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.5mld.countryOfResidenceYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(ntRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def countryOfResidenceInUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(CountryOfResidenceInTheUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def countryOfResidence(index: Int): Option[AnswerRow] = userAnswers.get(CountryOfResidenceInTheUkYesNoPage(index)) flatMap {
    case false => userAnswers.get(CountryOfResidencePage(index)) map {
      x =>
        AnswerRow(
          "charity.5mld.countryOfResidence.checkYourAnswersLabel",
          HtmlFormat.escape(checkAnswersFormatters.country(x)),
          Some(ntRts.CountryOfResidenceController.onPageLoad(index, draftId).url),
          canEdit = canEdit
        )
    }
    case _ => None
  }

  def addressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.addressYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(rts.AddressYesNoController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def amountDiscretionYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AmountDiscretionYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.discretionYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(rts.AmountDiscretionYesNoController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def charityName(index: Int): Option[AnswerRow] = userAnswers.get(CharityNamePage(index)) map {
    x =>
      AnswerRow(
        "charity.name.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(rts.CharityNameController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def howMuchIncome(index: Int): Option[AnswerRow] = userAnswers.get(HowMuchIncomePage(index)) map {
    x =>
      AnswerRow(
        "charity.shareOfIncome.checkYourAnswersLabel",
        checkAnswersFormatters.percentage(x.toString),
        Some(rts.HowMuchIncomeController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def charityInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(CharityInternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "charity.internationalAddress.checkYourAnswersLabel",
        checkAnswersFormatters.internationalAddress(x),
        Some(rts.CharityInternationalAddressController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def charityAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(CharityAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "charity.ukAddress.checkYourAnswersLabel",
        checkAnswersFormatters.ukAddress(x),
        Some(rts.CharityAddressUKController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def addressInTheUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressInTheUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "charity.addressInTheUkYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(rts.AddressInTheUkYesNoController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.charityBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

}

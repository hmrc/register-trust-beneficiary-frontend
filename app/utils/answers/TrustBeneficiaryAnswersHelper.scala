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

import controllers.register.beneficiaries.charityortrust.trust.mld5.{routes => ntRoutes}
import controllers.register.beneficiaries.charityortrust.trust.routes
import models.UserAnswers
import pages.register.beneficiaries.charityortrust.trust._
import pages.register.beneficiaries.charityortrust.trust.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import sections.beneficiaries.TrustBeneficiaries
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class TrustBeneficiaryAnswersHelper @Inject()(checkAnswersFormatters: CheckAnswersFormatters)
                                             (userAnswers: UserAnswers,
                                              draftId: String,
                                              canEdit: Boolean)
                                             (implicit messages: Messages) {

  private def trustBeneficiaryName(index: Int, userAnswers: UserAnswers): String = {
    userAnswers.get(NamePage(index)).getOrElse("")
  }

  def trustBeneficiaries: Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(TrustBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = trustBeneficiaryRows(index)

        AnswerSection(
          Some(Messages("answerPage.section.trustBeneficiary.subheading", index + 1)),
          questions,
          None
        )
    }
  }

  def trustBeneficiaryRows(index: Int): Seq[AnswerRow] = {
    Seq(
      trustBeneficiaryName(index),
      trustBeneficiaryDiscretionYesNo(index),
      trustBeneficiaryShareOfIncome(index),
      trustBeneficiaryCountryOfResidenceYesNo(index),
      trustBeneficiaryCountryOfResidenceInTheUkYesNo(index),
      trustBeneficiaryCountryOfResidence(index),
      trustBeneficiaryAddressYesNo(index),
      trustBeneficiaryAddressUKYesNo(index),
      trustBeneficiaryAddressUK(index),
      trustBeneficiaryAddressInternational(index)
    ).flatten
  }

  def trustBeneficiaryAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trustBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(routes.AddressUKYesNoController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(AddressUKPage(index)) map {
    x =>
      AnswerRow(
        "site.address.uk.checkYourAnswersLabel",
        checkAnswersFormatters.ukAddress(x),
        Some(routes.AddressUKController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryAddressInternational(index: Int): Option[AnswerRow] = userAnswers.get(AddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "site.address.international.checkYourAnswersLabel",
        checkAnswersFormatters.internationalAddress(x),
        Some(routes.AddressInternationalController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trustBeneficiaryAddressYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(routes.AddressYesNoController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryShareOfIncome(index: Int): Option[AnswerRow] = userAnswers.get(ShareOfIncomePage(index)) map {
    x =>
      AnswerRow(
        "trustBeneficiaryShareOfIncome.checkYourAnswersLabel",
        checkAnswersFormatters.percentage(x.toString),
        Some(routes.ShareOfIncomeController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryDiscretionYesNo(index: Int): Option[AnswerRow] = userAnswers.get(DiscretionYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trustBeneficiaryDiscretionYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(routes.DiscretionYesNoController.onPageLoad(index, draftId).url),
        checkAnswersFormatters.indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(NamePage(index)) map {
    x =>
      AnswerRow(
        "trustBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(routes.NameController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryCountryOfResidenceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(CountryOfResidenceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trust.5mld.countryOfResidenceYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(ntRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryCountryOfResidenceInTheUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(CountryOfResidenceInTheUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trust.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
        checkAnswersFormatters.yesOrNo(x),
        Some(ntRoutes.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
        trustBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trustBeneficiaryCountryOfResidence(index: Int): Option[AnswerRow] = userAnswers.get(CountryOfResidenceInTheUkYesNoPage(index)) flatMap {
    case false => userAnswers.get(CountryOfResidencePage(index)) map {
      x =>
        AnswerRow(
          "trust.5mld.countryOfResidence.checkYourAnswersLabel",
          HtmlFormat.escape(checkAnswersFormatters.country(x)),
          Some(ntRoutes.CountryOfResidenceController.onPageLoad(index, draftId).url),
          trustBeneficiaryName(index, userAnswers),
          canEdit = canEdit
        )
    }
    case _ => None
  }
}

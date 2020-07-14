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

package utils

import javax.inject.Inject
import models.{NormalMode, UserAnswers}
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}
import CheckAnswersFormatters._

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {


  def individualBeneficiaries: Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(IndividualBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = individualBeneficiaryRows(index)

        AnswerSection(Some(Messages("answerPage.section.individualBeneficiary.subheading") + " " + (index + 1)),
          questions, if (index == 0) {
            Some(Messages("answerPage.section.beneficiaries.heading"))
          } else {
            None
          })
    }
  }

  def individualBeneficiaryRows(index: Int): Seq[AnswerRow] = {
    Seq(
      individualBeneficiaryName(index),
      individualBeneficiaryRoleInCompany(index),
      individualBeneficiaryDateOfBirthYesNo(index),
      individualBeneficiaryDateOfBirth(index),
      individualBeneficiaryIncomeYesNo(index),
      individualBeneficiaryIncome(index),
      individualBeneficiaryNationalInsuranceYesNo(index),
      individualBeneficiaryNationalInsuranceNumber(index),
      individualBeneficiaryAddressYesNo(index),
      individualBeneficiaryAddressUKYesNo(index),
      individualBeneficiaryAddressUK(index),
      individualBeneficiaryVulnerableYesNo(index)
    ).flatten
  }

  def classOfBeneficiaries(individualBeneficiariesExist: Boolean): Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(ClassOfBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = Seq(
          classBeneficiaryDescription(index)
        ).flatten

        val sectionKey = if (index == 0 && !individualBeneficiariesExist) {
          Some(Messages("answerPage.section.beneficiaries.heading"))
        } else {
          None
        }

        AnswerSection(Some(Messages("answerPage.section.classOfBeneficiary.subheading") + " " + (index + 1)),
          questions, sectionKey)
    }
  }

  def classBeneficiaryDescription(index: Int): Option[AnswerRow] = userAnswers.get(ClassBeneficiaryDescriptionPage(index)) map {
    x =>
      AnswerRow(
        "classBeneficiaryDescription.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.beneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }


  def individualBeneficiaryAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.AddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def addABeneficiary(): Option[AnswerRow] = userAnswers.get(AddABeneficiaryPage) map {
    x =>
      AnswerRow(
        "addABeneficiary.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"addABeneficiary.$x")),
        Some(controllers.register.beneficiaries.routes.AddABeneficiaryController.onPageLoad(draftId).url),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryVulnerableYesNo(index: Int): Option[AnswerRow] = userAnswers.get(VulnerableYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryVulnerableYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.VulnerableYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(AddressUKPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.AddressUKController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.AddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryNationalInsuranceNumber(index: Int): Option[AnswerRow] =
    userAnswers.get(NationalInsuranceNumberPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.NationalInsuranceNumberController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryNationalInsuranceYesNo(index: Int): Option[AnswerRow] = userAnswers.get(NationalInsuranceYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.NationalInsuranceYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryIncome(index: Int): Option[AnswerRow] = userAnswers.get(IncomePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncome.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.IncomeController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryIncomeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IncomeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryIncomeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.IncomeYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(DateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.DateOfBirthController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(DateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.DateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryName(index: Int): Option[AnswerRow] = userAnswers.get(NamePage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.NameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def individualBeneficiaryRoleInCompany(index: Int): Option[AnswerRow] = userAnswers.get(RoleInCompanyPage(index)) map {
    x =>
      AnswerRow(
        "individualBeneficiaryRoleInCompany.checkYourAnswersLabel",
        HtmlFormat.escape(x.toString),
        Some(controllers.register.beneficiaries.individualBeneficiary.routes.RoleInCompanyController.onPageLoad(NormalMode, index, draftId).url),
        indBeneficiaryName(index, userAnswers),
        canEdit = canEdit
      )
  }

}
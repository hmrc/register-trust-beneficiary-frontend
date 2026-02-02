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

package repositories

import mapping.registration.BeneficiariesMapper
import models._
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.answers._
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class SubmissionSetFactory @Inject() (
  beneficiariesMapper: BeneficiariesMapper,
  individualBeneficiaryAnswersHelper: IndividualBeneficiaryAnswersHelper,
  classOfBeneficiaryAnswersHelper: ClassOfBeneficiaryAnswersHelper,
  charityBeneficiaryAnswersHelper: CharityBeneficiaryAnswersHelper,
  trustBeneficiaryAnswersHelper: TrustBeneficiaryAnswersHelper,
  companyBeneficiaryAnswersHelper: CompanyBeneficiaryAnswersHelper,
  largeBeneficiaryAnswersHelper: EmploymentRelatedBeneficiaryAnswersHelper,
  otherBeneficiaryAnswersHelper: OtherBeneficiaryAnswersHelper
) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet =

    RegistrationSubmission.DataSet(
      Json.toJson(userAnswers),
      mappedData(userAnswers),
      answerSections(userAnswers)
    )

  private def mappedData(userAnswers: UserAnswers): List[RegistrationSubmission.MappedPiece] =
    beneficiariesMapper.build(userAnswers) match {
      case Some(assets) => List(RegistrationSubmission.MappedPiece("trust/entities/beneficiary", Json.toJson(assets)))
      case _            => List.empty
    }

  def answerSections(
    userAnswers: UserAnswers
  )(implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    val entitySections = List(
      individualBeneficiaryAnswersHelper.beneficiaries(userAnswers),
      classOfBeneficiaryAnswersHelper.beneficiaries(userAnswers),
      charityBeneficiaryAnswersHelper.beneficiaries(userAnswers),
      trustBeneficiaryAnswersHelper.beneficiaries(userAnswers),
      companyBeneficiaryAnswersHelper.beneficiaries(userAnswers),
      largeBeneficiaryAnswersHelper.beneficiaries(userAnswers),
      otherBeneficiaryAnswersHelper.beneficiaries(userAnswers)
    ).flatten.flatten

    if (entitySections.nonEmpty) {
      val updatedFirstSection = entitySections.head.copy(sectionKey = Some("answerPage.section.beneficiaries.heading"))
      val updatedSections     = updatedFirstSection :: entitySections.tail
      updatedSections.map(convertForSubmission)
    } else {
      List.empty
    }

  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection =
    RegistrationSubmission.AnswerSection(
      headingKey = section.headingKey,
      rows = section.rows.map(convertForSubmission),
      sectionKey = section.sectionKey,
      headingArgs = section.headingArgs.map(_.toString)
    )

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow =
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)

}

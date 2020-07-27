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

import javax.inject.Inject
import models.UserAnswers
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import play.api.i18n.Messages
import sections.beneficiaries.ClassOfBeneficiaries
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class ClassOfBeneficiariesAnswersHelper @Inject()(countryOptions: CountryOptions)
                                                 (userAnswers: UserAnswers,
                                                  draftId: String,
                                                  canEdit: Boolean)
                                                 (implicit messages: Messages) {

  def classOfBeneficiaries: Option[Seq[AnswerSection]] = {

    for {
      beneficiaries <- userAnswers.get(ClassOfBeneficiaries)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = classOfBeneficiariesRows(index)

        AnswerSection(
          Some(Messages("answerPage.section.classOfBeneficiary.subheading") + " " + (index + 1)),
          questions,
          None
        )
    }
  }

  private def classOfBeneficiariesRows(index: Int): Seq[AnswerRow] = {
    val helper = new CheckYourAnswersHelper(countryOptions)(userAnswers, canEdit = canEdit)

    Seq(
      helper.stringQuestion(
        ClassBeneficiaryDescriptionPage(index),
        "classBeneficiaryDescription",
        Some(controllers.register.beneficiaries.classofbeneficiaries.routes.ClassBeneficiaryDescriptionController.onPageLoad(index, draftId).url)
      )
    ).flatten
  }

}

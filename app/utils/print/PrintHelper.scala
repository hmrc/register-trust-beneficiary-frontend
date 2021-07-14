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

import models.UserAnswers
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

trait PrintHelper {

  val beneficiaryType: String

  def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
             (implicit messages: Messages): Seq[AnswerRow]

  def printSection(userAnswers: UserAnswers, name: Option[String], index: Int, draftId: String)
                  (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      headingKey = Some(s"answerPage.section.$beneficiaryType.subheading"),
      rows = answers(userAnswers, name.getOrElse(""), index, draftId),
      headingArgs = Seq(index + 1)
    )
  }

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                         (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      rows = answers(userAnswers, name, index, draftId)
    )
  }

}

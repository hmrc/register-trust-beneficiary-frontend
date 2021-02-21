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

import models.UserAnswers
import pages.QuestionPage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import utils.print.PrintHelper
import viewmodels.AnswerSection
import viewmodels.addAnother.ViewModel

abstract class AnswersHelper[T <: ViewModel](printHelper: PrintHelper) {

  val beneficiaryType: QuestionPage[List[T]]

  def beneficiaries(userAnswers: UserAnswers)
                   (implicit messages: Messages, rds: Reads[T]): Option[Seq[AnswerSection]] = {
    for {
      beneficiaries <- userAnswers.get(beneficiaryType)
      indexed = beneficiaries.zipWithIndex
    } yield indexed.map {
      case (beneficiary, index) =>
        printHelper.printSection(userAnswers, beneficiary.label, index, userAnswers.draftId)
    }
  }
}

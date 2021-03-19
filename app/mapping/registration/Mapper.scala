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

package mapping.registration

import mapping.reads.Beneficiary
import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.Reads

trait Mapper[A, B <: Beneficiary] {

  def build(userAnswers: UserAnswers)(implicit rds: Reads[B]): Option[List[A]] = {

    userAnswers.get(section).getOrElse(List.empty) match {
      case Nil => None
      case list => Some(list.map(beneficiaryType))
    }
  }

  def section: QuestionPage[List[B]]

  def beneficiaryType(beneficiary: B): A

}

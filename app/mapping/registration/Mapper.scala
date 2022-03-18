/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.{JsPath, Reads}

trait Mapper[A, B <: Beneficiary] {

  case object Beneficiaries extends QuestionPage[List[B]] {
    override def path: JsPath = jsPath
  }

  def build(userAnswers: UserAnswers)(implicit rds: Reads[B]): Option[List[A]] = {

    userAnswers.get(Beneficiaries).getOrElse(List.empty) match {
      case Nil => None
      case list => Some(list.map(beneficiaryType))
    }
  }

  def jsPath: JsPath

  def beneficiaryType(beneficiary: B): A

}

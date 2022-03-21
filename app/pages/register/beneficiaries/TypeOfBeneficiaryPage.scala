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

package pages.register.beneficiaries

import models.Status.InProgress
import models.{Status, UserAnswers}
import play.api.libs.json._

import scala.util.{Success, Try}

trait TypeOfBeneficiaryPage {

  def cleanupLastIfInProgress(userAnswers: UserAnswers, paths: Seq[JsPath]): Try[UserAnswers] = {

    def status(beneficiary: JsValue) = beneficiary.transform((__ \ "status").json.pick) match {
      case JsSuccess(value, _) => value.as[Status]
      case _ => InProgress
    }

    paths.foldLeft[Try[UserAnswers]](Success(userAnswers))((acc, path) => {
      acc match {
        case Success(value) => value.getAtPath[JsArray](path).getOrElse(JsArray()) match {
          case x if x.value.nonEmpty && status(x.value.last) == InProgress => value.deleteAtPath(path \ (x.value.size - 1))
          case _ => Success(value)
        }
        case _ => acc
      }
    })
  }

}

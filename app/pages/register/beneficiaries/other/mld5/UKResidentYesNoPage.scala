/*
 * Copyright 2026 HM Revenue & Customs
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

package pages.register.beneficiaries.other.mld5

import errors.TrustErrors
import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.beneficiaries.{Beneficiaries, OtherBeneficiaries}
import utils.Constants.GB

final case class UKResidentYesNoPage(index: Int) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ Beneficiaries \ OtherBeneficiaries \ index \ toString

  override def toString: String = "ukResidentYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Either[TrustErrors, UserAnswers] =
    value match {
      case Some(true) => userAnswers.set(CountryOfResidencePage(index), GB)
      case _          => super.cleanup(value, userAnswers)
    }

}

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

package pages.register.beneficiaries.charityortrust

import errors.TrustErrors
import models.UserAnswers
import models.registration.pages.CharityOrTrust
import models.registration.pages.CharityOrTrust._
import pages.QuestionPage
import pages.register.beneficiaries.TypeOfBeneficiaryPage
import play.api.libs.json._
import sections.beneficiaries._

case object CharityOrTrustPage extends QuestionPage[CharityOrTrust] with TypeOfBeneficiaryPage {

  override def path: JsPath = JsPath \ Beneficiaries \ toString

  override def toString: String = "charityortrust"

  override def cleanup(value: Option[CharityOrTrust], userAnswers: UserAnswers): Either[TrustErrors, UserAnswers] = {

    def paths: Seq[JsPath] = Seq(
      CharityBeneficiaries.path,
      TrustBeneficiaries.path
    )

    value match {
      case Some(Charity) => cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == CharityBeneficiaries.path))
      case Some(Trust) => cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == TrustBeneficiaries.path))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}

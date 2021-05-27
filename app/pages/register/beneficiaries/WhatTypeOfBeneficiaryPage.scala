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

package pages.register.beneficiaries

import models.UserAnswers
import models.registration.pages.WhatTypeOfBeneficiary
import models.registration.pages.WhatTypeOfBeneficiary._
import pages.QuestionPage
import play.api.libs.json._
import sections.beneficiaries._

import scala.util.Try

case object WhatTypeOfBeneficiaryPage extends QuestionPage[WhatTypeOfBeneficiary] with TypeOfBeneficiaryPage {

  override def path: JsPath = JsPath \ Beneficiaries \ toString

  override def toString: String = "whatTypeOfBeneficiary"

  override def cleanup(value: Option[WhatTypeOfBeneficiary], userAnswers: UserAnswers): Try[UserAnswers] = {

    def paths: Seq[JsPath] = Seq(
      IndividualBeneficiaries.path,
      ClassOfBeneficiaries.path,
      CharityBeneficiaries.path,
      TrustBeneficiaries.path,
      CompanyBeneficiaries.path,
      LargeBeneficiaries.path,
      OtherBeneficiaries.path,
    )

    value match {
      case Some(Individual) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == IndividualBeneficiaries.path))
      case Some(ClassOfBeneficiary) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == ClassOfBeneficiaries.path))
      case Some(CharityOrTrust) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(x => x == CharityBeneficiaries.path || x == TrustBeneficiaries.path))
      case Some(Charity) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == CharityBeneficiaries.path))
      case Some(Trust) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == TrustBeneficiaries.path))
      case Some(CompanyOrEmployment) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(x => x == CompanyBeneficiaries.path || x == LargeBeneficiaries.path))
      case Some(Company) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == CompanyBeneficiaries.path))
      case Some(Employment) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == LargeBeneficiaries.path))
      case Some(Other) =>
        cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == OtherBeneficiaries.path))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}

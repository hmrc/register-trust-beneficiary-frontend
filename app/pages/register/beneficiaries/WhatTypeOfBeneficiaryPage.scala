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

import models.Status.InProgress
import models.registration.pages.WhatTypeOfBeneficiary
import models.registration.pages.WhatTypeOfBeneficiary._
import models.{Status, UserAnswers}
import pages.QuestionPage
import play.api.libs.json._
import sections.beneficiaries._

import scala.util.{Success, Try}

case object WhatTypeOfBeneficiaryPage extends QuestionPage[WhatTypeOfBeneficiary] {

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

    def cleanupLastIfInProgress(paths: Seq[JsPath]): Try[UserAnswers] = {

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

    value match {
      case Some(Individual) => cleanupLastIfInProgress(paths.filterNot(_ == IndividualBeneficiaries.path))
      case Some(ClassOfBeneficiary) => cleanupLastIfInProgress(paths.filterNot(_ == ClassOfBeneficiaries.path))
      case Some(CharityOrTrust) => cleanupLastIfInProgress(paths.filterNot(x => x == CharityBeneficiaries.path || x == TrustBeneficiaries.path))
      case Some(Charity) => cleanupLastIfInProgress(paths.filterNot(_ == CharityBeneficiaries.path))
      case Some(Trust) => cleanupLastIfInProgress(paths.filterNot(_ == TrustBeneficiaries.path))
      case Some(CompanyOrEmployment) => cleanupLastIfInProgress(paths.filterNot(x => x == CompanyBeneficiaries.path || x == LargeBeneficiaries.path))
      case Some(Company) => cleanupLastIfInProgress(paths.filterNot(_ == CompanyBeneficiaries.path))
      case Some(Employment) => cleanupLastIfInProgress(paths.filterNot(_ == LargeBeneficiaries.path))
      case Some(Other) => cleanupLastIfInProgress(paths.filterNot(_ == OtherBeneficiaries.path))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}

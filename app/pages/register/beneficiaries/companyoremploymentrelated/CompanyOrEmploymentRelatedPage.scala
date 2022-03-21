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

package pages.register.beneficiaries.companyoremploymentrelated

import models.CompanyOrEmploymentRelatedToAdd.{Company, EmploymentRelated}
import models.{CompanyOrEmploymentRelatedToAdd, UserAnswers}
import pages.QuestionPage
import pages.register.beneficiaries.TypeOfBeneficiaryPage
import play.api.libs.json.JsPath
import sections.beneficiaries.{CompanyBeneficiaries, LargeBeneficiaries}

import scala.util.Try

case object CompanyOrEmploymentRelatedPage extends QuestionPage[CompanyOrEmploymentRelatedToAdd] with TypeOfBeneficiaryPage {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "companyOrEmploymentRelated"

  override def cleanup(value: Option[CompanyOrEmploymentRelatedToAdd], userAnswers: UserAnswers): Try[UserAnswers] = {

    def paths: Seq[JsPath] = Seq(
      CompanyBeneficiaries.path,
      LargeBeneficiaries.path
    )

    value match {
      case Some(Company) => cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == CompanyBeneficiaries.path))
      case Some(EmploymentRelated) => cleanupLastIfInProgress(userAnswers, paths.filterNot(_ == LargeBeneficiaries.path))
      case _ => super.cleanup(value, userAnswers)
    }
  }
}

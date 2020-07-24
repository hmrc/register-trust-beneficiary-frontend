/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.register.beneficiaries

import models.ReadableUserAnswers
import play.api.libs.json.JsArray
import sections.beneficiaries._

trait AnyBeneficiaries {
  def isAnyBeneficiaryAdded(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty).nonEmpty ||
      userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty).nonEmpty ||
      userAnswers.get(CharityBeneficiaries).getOrElse(List.empty).nonEmpty ||
      userAnswers.get(TrustBeneficiaries).getOrElse(List.empty).nonEmpty ||
      userAnswers.get(CompanyBeneficiaries).getOrElse(List.empty).nonEmpty ||
      userAnswers.get(LargeBeneficiaries).getOrElse(JsArray()).value.nonEmpty ||
      userAnswers.get(OtherBeneficiaries).getOrElse(JsArray()).value.nonEmpty
  }
}

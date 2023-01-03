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

package forms

import forms.mappings.Mappings
import models.UserAnswers

import javax.inject.Inject
import play.api.data.Form

class NationalInsuranceNumberFormProvider @Inject() extends Mappings {

  def withPrefix(prefix: String, userAnswers: UserAnswers, index: Int): Form[String] =
    Form("value" -> nino(s"$prefix.error.required")
      .verifying(
        firstError(
          isNotEmpty("value", s"$prefix.error.required"),
          isNinoValid("value", s"$prefix.error.invalid"),
          isNinoDuplicated(userAnswers, index, s"$prefix.error.duplicate")

    )))
}

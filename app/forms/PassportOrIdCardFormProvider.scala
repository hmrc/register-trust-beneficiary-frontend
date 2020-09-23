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

/*
 * Copyright 2019 HM Revenue & Customs
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

import config.FrontendAppConfig
import forms.mappings.Mappings
import javax.inject.Inject
import models.registration.pages.PassportOrIdCardDetails
import play.api.data.Form
import play.api.data.Forms._


class PassportOrIdCardFormProvider @Inject()(appConfig: FrontendAppConfig) extends Mappings {

  val maxLengthCountyField = 100
  val maxLengthNumberField = 30

  def apply(prefix: String): Form[PassportOrIdCardDetails] = Form(
    mapping(
      "country" -> text(s"$prefix.country.error.required")
        .verifying(
          firstError(
            maxLength(maxLengthCountyField, s"$prefix.country.error.length"),
            isNotEmpty("country", s"$prefix.country.error.required")
          )
        ),
      "number" -> text(s"$prefix.number.error.required")
        .verifying(
          firstError(
            maxLength(maxLengthNumberField, s"$prefix.number.error.length"),
            regexp(Validation.passportOrIdCardNumberRegEx, s"$prefix.number.error.invalid"),
            isNotEmpty("number", s"$prefix.number.error.required")
          )
        ),
      "expiryDate" -> localDate(
        invalidKey     = s"$prefix.expiryDate.error.invalid",
        allRequiredKey = s"$prefix.expiryDate.error.required.all",
        twoRequiredKey = s"$prefix.expiryDate.error.required.two",
        requiredKey    = s"$prefix.expiryDate.error.required"
      ).verifying(firstError(
        maxDate(
          appConfig.maxPassportDate,
          s"$prefix.expiryDate.error.future", "day", "month", "year"
        ),
        minDate(
          appConfig.minDate,
          s"$prefix.expiryDate.error.past", "day", "month", "year"
        )
      ))

    )(PassportOrIdCardDetails.apply)(PassportOrIdCardDetails.unapply)
  )
}

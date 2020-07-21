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

package forms

import forms.behaviours.{IntFieldBehaviours, StringFieldBehaviours}
import org.scalacheck.Gen
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class HowMuchIncomeFormProviderSpec extends StringFieldBehaviours with IntFieldBehaviours {

  val requiredKey = "howMuchIncome.error.required"
  val lengthKey = "howMuchIncome.error.length"
  val maxLength = 12
  val zeroNumberkey = "howMuchIncome.error.zero"

  val form = new HowMuchIncomeFormProvider()()

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.onlyNumbersRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like intFieldWithMinimumWithGenerator(
      form,
      fieldName,
      1,
      Gen.const(0),
      FormError(fieldName, zeroNumberkey, Array("1"))
    )


    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}

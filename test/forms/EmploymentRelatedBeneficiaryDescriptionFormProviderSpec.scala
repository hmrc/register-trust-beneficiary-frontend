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

package forms

import forms.behaviours.{OptionalFieldBehaviours, StringFieldBehaviours}
import play.api.data.FormError
import wolfendale.scalacheck.regexp.RegexpGen

class EmploymentRelatedBeneficiaryDescriptionFormProviderSpec extends StringFieldBehaviours with OptionalFieldBehaviours {

  val messageKeyPrefix = "employmentRelatedBeneficiary.description"
  val form = new EmploymentRelatedBeneficiaryDescriptionFormProvider().withPrefix(messageKeyPrefix)

  val maxLength = 70
  val minLength = 1

  ".description" must {

    val fieldName = "description"
    val requiredKey = s"$messageKeyPrefix.error.required"
    val lengthKey = s"$messageKeyPrefix.error.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.descriptionRegex)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )
  }

  ".description1" must {

    val fieldName = "description1"
    val lengthKey = s"$messageKeyPrefix.error.length"
    val maxLength = 70

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.descriptionRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("description" -> "description", "description1" -> "  description1  "))
      result.value.value.description1 shouldBe Some("description1")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("description" -> "description", "description1" -> "  "))
      result.value.value.description1 shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("description" -> "description", "description1" -> ""))
      result.value.value.description1 shouldBe None
    }
  }

  ".description2" must {

    val fieldName = "description2"
    val lengthKey = s"$messageKeyPrefix.error.length"
    val maxLength = 70

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.descriptionRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("description" -> "description", "description2" -> "  description2  "))
      result.value.value.description2 shouldBe Some("description2")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("description" -> "description", "description2" -> "  "))
      result.value.value.description2 shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("description" -> "description", "description2" -> ""))
      result.value.value.description2 shouldBe None
    }
  }

  ".description3" must {

    val fieldName = "description3"
    val lengthKey = s"$messageKeyPrefix.error.length"
    val maxLength = 70

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.descriptionRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("description" -> "description", "description3" -> "  description3  "))
      result.value.value.description3 shouldBe Some("description3")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("description" -> "description", "description3" -> "  "))
      result.value.value.description3 shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("description" -> "description", "description3" -> ""))
      result.value.value.description3 shouldBe None
    }
  }

  ".description4" must {

    val fieldName = "description4"
    val lengthKey = s"$messageKeyPrefix.error.length"
    val maxLength = 70

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(
      form,
      fieldName,
      validDataGenerator = RegexpGen.from(Validation.descriptionRegex)
    )

    "bind whitespace trim values" in {
      val result = form.bind(Map("description" -> "description", "description4" -> "  description4  "))
      result.value.value.description4 shouldBe Some("description4")
    }

    "bind whitespace blank values" in {
      val result = form.bind(Map("description" -> "description", "description4" -> "  "))
      result.value.value.description4 shouldBe None
    }

    "bind whitespace no values" in {
      val result = form.bind(Map("description" -> "description", "description4" -> ""))
      result.value.value.description4 shouldBe None
    }
  }

}
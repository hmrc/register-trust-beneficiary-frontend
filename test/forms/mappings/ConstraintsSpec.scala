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

package forms.mappings

import generators.Generators
import models.UserAnswers
import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.validation.{Invalid, Valid, ValidationError}
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

class ConstraintsSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with Generators  with Constraints {


  "firstError" must {

    "return Valid when all constraints pass" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("foo")
      result mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }

    "return Invalid when the second constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.regexp", """^\w+$""")
    }

    "return Invalid for the first error when both constraints fail" in {
      val result = firstError(maxLength(-1, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.length", -1)
    }
  }

  "minimumValue" must {

    "return Valid for a number greater than the threshold" in {
      val result = minimumValue(1, "error.min").apply(2)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = minimumValue(1, "error.min").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      val result = minimumValue(1, "error.min").apply(0)
      result mustEqual Invalid("error.min", 1)
    }
  }

  "maximumValue" must {

    "return Valid for a number less than the threshold" in {
      val result = maximumValue(1, "error.max").apply(0)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = maximumValue(1, "error.max").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      val result = maximumValue(1, "error.max").apply(2)
      result mustEqual Invalid("error.max", 1)
    }
  }

  "regexp" must {

    "return Valid for an input that matches the expression" in {
      val result = regexp("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "return Invalid for an input that does not match the expression" in {
      val result = regexp("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid", """^\d+$""")
    }
  }

  "maxLength" must {

    "return Valid for a string shorter than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 9)
      result mustEqual Valid
    }

    "return Valid for an empty string" in {
      val result = maxLength(10, "error.length")("")
      result mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 10)
      result mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }
  }

  "maxDate" must {

    "return Valid for a date before or equal to the maximum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        max <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1))
        date <- datesBetween(LocalDate.of(2000, 1, 1), max)
      } yield (max, date)

      forAll(gen) {
        case (max, date) =>

          val result = maxDate(max, "error.future")(date)
          result mustEqual Valid
      }
    }

    "return Invalid for a date after the maximum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        max <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1))
        date <- datesBetween(max.plusDays(1), LocalDate.of(3000, 1, 2))
      } yield (max, date)

      forAll(gen) {
        case (max, date) =>

          val result = maxDate(max, "error.future", "foo")(date)
          result mustEqual Invalid("error.future", "foo")
      }
    }
  }

  "minDate" must {

    "return Valid for a date after or equal to the minimum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        min <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1))
        date <- datesBetween(min, LocalDate.of(3000, 1, 1))
      } yield (min, date)

      forAll(gen) {
        case (min, date) =>

          val result = minDate(min, "error.past", "foo")(date)
          result mustEqual Valid
      }
    }

    "return Invalid for a date before the minimum" in {

      val gen: Gen[(LocalDate, LocalDate)] = for {
        min <- datesBetween(LocalDate.of(2000, 1, 2), LocalDate.of(3000, 1, 1))
        date <- datesBetween(LocalDate.of(2000, 1, 1), min.minusDays(1))
      } yield (min, date)

      forAll(gen) {
        case (min, date) =>

          val result = minDate(min, "error.past", "foo")(date)
          result mustEqual Invalid("error.past", "foo")
      }
    }

    "uniquePassportNumber" must {
      "return valid when isPassportNumberDuplicated is true" in {
        val userAnswers = UserAnswers(draftId = "", internalAuthId = "")
        val result = isPassportNumberDuplicated(userAnswers, 0, "number.error.duplicate")("502135326")
        result mustEqual Valid
      }

      "return invalid when isPassportNumberDuplicated is false" in {
        val json =
          """
            |{
            |   "beneficiaries":{
            |      "individualBeneficiaries":[
            |         {
            |            "name":{
            |               "firstName":"Jon",
            |               "lastName":"Jameson"
            |            },
            |            "dateOfBirthYesNo":false,
            |            "incomeYesNo":true,
            |            "countryOfNationalityYesNo":false,
            |            "nationalInsuranceNumberYesNo":false,
            |            "countryOfResidenceYesNo":false,
            |            "addressYesNo":true,
            |            "addressUKYesNo":true,
            |            "ukAddress":{
            |               "line1":"Line 1",
            |               "line2":"Line 2",
            |               "line3":"Line 3",
            |               "line4":"Line 4",
            |               "postcode":"NE98 1EZ"
            |            },
            |            "passportDetailsYesNo":true,
            |            "passportDetails":{
            |               "country":"DZ",
            |               "cardNumber":"502135326",
            |               "expiryDate":"2029-02-12"
            |            },
            |            "mentalCapacityYesNo":"dontKnow",
            |            "vulnerableYesNo":false,
            |            "status":"completed"
            |         }
            |      ]
            |   }
            |}
            |""".stripMargin
        val userAnswers = UserAnswers(draftId = "", data = Json.parse(json).as[JsObject], internalAuthId = "")
        val result = isPassportNumberDuplicated(userAnswers, 1, "number.error.duplicate")("502135326")
        result mustEqual Invalid(List(ValidationError(List("number.error.duplicate"))))
      }
    }

    "uniqueIDNumber" must {
      "return valid when isPassportNumberDuplicated is true" in {
        val userAnswers = UserAnswers(draftId = "", internalAuthId = "")
        val result = isIDNumberDuplicated(userAnswers, 0, "number.error.duplicate")("502135326")
        result mustEqual Valid
      }

      "return invalid when isIDNumberDuplicated is false" in {
        val json =
          """
            |{
            |   "beneficiaries":{
            |      "individualBeneficiaries":[
            |         {
            |            "name":{
            |               "firstName":"Jon",
            |               "lastName":"Jameson"
            |            },
            |            "dateOfBirthYesNo":false,
            |            "incomeYesNo":true,
            |            "countryOfNationalityYesNo":false,
            |            "nationalInsuranceNumberYesNo":false,
            |            "countryOfResidenceYesNo":false,
            |            "addressYesNo":true,
            |            "addressUKYesNo":true,
            |            "ukAddress":{
            |               "line1":"Line 1",
            |               "line2":"Line 2",
            |               "line3":"Line 3",
            |               "line4":"Line 4",
            |               "postcode":"NE98 1EZ"
            |            },
            |            "passportDetailsYesNo":false,
            |            "idCardDetailsYesNo":false,
            |            "idCardDetails":{
            |               "country":"DZ",
            |               "cardNumber":"018765432",
            |               "expiryDate":"2029-02-12"
            |            },
            |            "mentalCapacityYesNo":"dontKnow",
            |            "vulnerableYesNo":false,
            |            "status":"completed"
            |         }
            |      ]
            |   }
            |}
            |""".stripMargin
        val userAnswers = UserAnswers(draftId = "", data = Json.parse(json).as[JsObject], internalAuthId = "")
        val result = isIDNumberDuplicated(userAnswers, 1, "number.error.duplicate")("018765432")
        result mustEqual Invalid(List(ValidationError(List("number.error.duplicate"))))
      }
    }

    "uniqueNino" should {

      val existingSettlorNinos: Seq[String] = Seq("AA123456C", "AR123456A", "AE123456C")

      "return Valid if the NINO is unique" in {

        val result = uniqueNino("error.duplicate", existingSettlorNinos)("AC123456C")
        result mustEqual Valid
      }

      "return Invalid if the NINO is already in the existingSettlorNinos" in {

        val result = uniqueNino("error.duplicate", existingSettlorNinos)("AR123456A")
        result mustEqual Invalid("error.duplicate")
      }
    }
  }
}

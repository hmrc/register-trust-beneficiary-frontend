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

import java.time.LocalDate
import forms.Validation
import models.UserAnswers
import pages.register.beneficiaries.individual.{NationalInsuranceNumberPage, PassportDetailsPage, IDCardDetailsPage}
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.libs.json.{JsArray, JsString, JsSuccess}
import sections.beneficiaries.IndividualBeneficiaries
import uk.gov.hmrc.domain.Nino

import scala.util.matching.Regex

trait Constraints {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint {
      input =>
        constraints
          .map(_.apply(input))
          .find(_ != Valid)
          .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum) {
          Valid
        } else {
          Invalid(errorKey, minimum)
        }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, maximum)
        }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint {
      input =>

        import ev._

        if (input >= minimum && input <= maximum) {
          Valid
        } else {
          Invalid(errorKey, minimum, maximum)
        }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _ =>
        Invalid(errorKey, regex)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _ =>
        Invalid(errorKey, maximum)
    }

  protected def minLength(minimum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length >= minimum =>
        Valid
      case _ =>
        Invalid(errorKey, minimum)
    }

  protected def isNotEmpty(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.trim.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey, value)
    }

  protected def isNinoValid(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if Nino.isValid(str)=>
        Valid
      case _ =>
        Invalid(errorKey, value)
    }

  protected def isNinoDuplicated(userAnswers: UserAnswers, index: Int, errorKey: String): Constraint[String] =
    Constraint {
      nino =>
        userAnswers.data.transform(IndividualBeneficiaries.path.json.pick[JsArray]) match {
          case JsSuccess(beneficiaries, _) =>

            val uniqueNino = beneficiaries.value.zipWithIndex.forall( beneficiary =>
              !((beneficiary._1 \\ NationalInsuranceNumberPage.key).contains(JsString(nino)) && beneficiary._2 != index)
            )

            if (uniqueNino) {
              Valid
            } else {
              Invalid(errorKey)
            }
          case _ =>
            Valid
        }
    }

  protected def uniqueNino(errorKey: String, existingSettlorNinos: Seq[String]): Constraint[String] =
    Constraint {
      nino =>
        if (existingSettlorNinos.contains(nino)) {
          Invalid(errorKey)
        } else {
          Valid
        }
    }

  protected def isPassportNumberDuplicated(userAnswers: UserAnswers, index: Int, errorKey: String): Constraint[String] =
    Constraint {
      passportNumber =>
        userAnswers.data.transform(IndividualBeneficiaries.path.json.pick[JsArray]) match {
          case JsSuccess(beneficiaries, _) =>
            val uniquePassportNumber = beneficiaries.value.zipWithIndex.forall { beneficiary =>
              !((beneficiary._1 \ PassportDetailsPage.key \ PassportDetailsPage.passportNumberKey).toOption.fold(false)(_.as[String] == (passportNumber)) && beneficiary._2 != index)
            }

            if (uniquePassportNumber) {
              Valid
            } else {
              Invalid(errorKey)
            }
          case _ =>
            Valid
        }
    }

  protected def isIDNumberDuplicated(userAnswers: UserAnswers, index: Int, errorKey: String): Constraint[String] =
    Constraint {
      idNumber =>
        userAnswers.data.transform(IndividualBeneficiaries.path.json.pick[JsArray]) match {
          case JsSuccess(beneficiaries, _) =>

            val uniqueIDNumber = beneficiaries.value.zipWithIndex.forall { beneficiary =>
                !((beneficiary._1 \ IDCardDetailsPage.key \ IDCardDetailsPage.idNumberKey).toOption.fold(false)(_.as[String].equals(idNumber)) && beneficiary._2 != index)
            }

            if (uniqueIDNumber) {
              Valid
            } else {
              Invalid(errorKey)
            }
          case _ =>
            Valid
        }
    }


  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _ =>
        Valid
    }

  protected def wholeNumber(errorKey: String) : Constraint[String] = {

    val regex: Regex = Validation.decimalCheck.r

    Constraint {
      case regex(_*) => Valid
      case _ =>  Invalid(errorKey)
    }
  }

  protected def nonEmptyString(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.trim.nonEmpty =>
        Valid
      case _ =>
        Invalid(errorKey, value)
    }

}

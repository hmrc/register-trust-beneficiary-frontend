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

package models

import errors.{ServerError, TrustErrors}
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import pages.register.beneficiaries.charityortrust.CharityOrTrustPage
import pages.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import play.api.Logging
import play.api.libs.json._
import queries.{Gettable, Settable}
import sections.beneficiaries._

trait ReadableUserAnswers {
  val data: JsObject
  val isTaxable: Boolean = true

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    getAtPath(page.path)

  def getAtPath[A](path: JsPath)(implicit rds: Reads[A]): Option[A] =
    Reads.at(path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(_)          => None
    }

  val beneficiaries: Beneficiaries = models.Beneficiaries(
    this.get(IndividualBeneficiaries).getOrElse(List.empty),
    this.get(ClassOfBeneficiaries).getOrElse(List.empty),
    this.get(CharityBeneficiaries).getOrElse(List.empty),
    this.get(TrustBeneficiaries).getOrElse(List.empty),
    this.get(CompanyBeneficiaries).getOrElse(List.empty),
    this.get(LargeBeneficiaries).getOrElse(List.empty),
    this.get(OtherBeneficiaries).getOrElse(List.empty)
  )

  val isAnyBeneficiaryAdded: Boolean =
    beneficiaries.individuals.nonEmpty ||
      beneficiaries.unidentified.nonEmpty ||
      beneficiaries.charities.nonEmpty ||
      beneficiaries.trusts.nonEmpty ||
      beneficiaries.companies.nonEmpty ||
      beneficiaries.large.nonEmpty ||
      beneficiaries.other.nonEmpty

}

case class ReadOnlyUserAnswers(data: JsObject) extends ReadableUserAnswers

object ReadOnlyUserAnswers {
  implicit lazy val formats: OFormat[ReadOnlyUserAnswers] = Json.format[ReadOnlyUserAnswers]
}

final case class UserAnswers(
  draftId: String,
  data: JsObject = Json.obj(),
  internalAuthId: String,
  override val isTaxable: Boolean = true
) extends ReadableUserAnswers with Logging {

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Either[TrustErrors, UserAnswers] = {

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Right(jsValue)
      case JsError(_)            =>
        logger.error(s"[UserAnswers][set] Unable to set path ${page.path} due to errors")
        Left(ServerError())
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](query: Settable[A]): Either[TrustErrors, UserAnswers] = {

    val updatedData = data.removeObject(query.path) match {
      case JsSuccess(jsValue, _) =>
        Right(jsValue)
      case JsError(_)            =>
        Right(data)
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      query.cleanup(None, updatedAnswers)
    }
  }

  def deleteAtPath(path: JsPath): Either[TrustErrors, UserAnswers] =
    data
      .removeObject(path)
      .map(obj => copy(data = obj))
      .fold(
        _ => Right(this),
        result => Right(result)
      )

  def removeBeneficiaryTypeAnswers(): Either[TrustErrors, UserAnswers] = this
    .remove(WhatTypeOfBeneficiaryPage)
    .flatMap(_.remove(CharityOrTrustPage))
    .flatMap(_.remove(CompanyOrEmploymentRelatedPage))

}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "internalId").read[String] and
        (__ \ "isTaxable").readWithDefault[Boolean](true)
    )(UserAnswers.apply _)
  }

  implicit lazy val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "internalId").write[String] and
        (__ \ "isTaxable").write[Boolean]
    )(unlift(UserAnswers.unapply))
  }

}
